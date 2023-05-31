package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository.GameUpdateType
import com.denisrebrof.springboottest.fight.domain.model.FighterState
import com.denisrebrof.springboottest.fight.domain.model.GameState
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StartGameHandler @Autowired constructor(
    private val gamesRepository: FightGamesRepository,
    private val notificationUseCase: SendUserNotificationUseCase,
) : DisposableService() {

    companion object {
        private const val GAME_START_DELAY: Long = 3000L
    }

    override val handler: Disposable = gamesRepository
        .getUpdates()
        .filter { it.type == GameUpdateType.Created }
        .map(FightGamesRepository.GameUpdate::matchId)
        .delay(GAME_START_DELAY, TimeUnit.MILLISECONDS)
        .subscribeDefault(::startMatchIfReady)

    private fun startMatchIfReady(matchId: String) {
        val game = gamesRepository.get(matchId) ?: return
        if (game.state != GameState.Preparing)
            return

        game.state = GameState.Playing
        val notification = GameState.Playing.id.toString().let(NotificationContent::Data)
        game.playerStates.forEach { (playerId, state) ->
            state.state = FighterState.Fighting(position = state.state.position)
            notificationUseCase.send(playerId, WSCommand.FightGameStateUpdate.id, notification)
        }
    }
}