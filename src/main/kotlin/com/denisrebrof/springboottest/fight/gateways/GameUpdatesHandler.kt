package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository
import com.denisrebrof.springboottest.fight.domain.model.FightGame
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameUpdatesHandler @Autowired constructor(
    private val notificationUseCase: SendUserNotificationUseCase,
    private val fightGamesRepository: FightGamesRepository
) : DisposableService() {
    override val handler: Disposable
        get() = fightGamesRepository
            .getUpdates()
            .map(FightGamesRepository.GameUpdate::game)
            .onBackpressureBuffer()
            .subscribeDefault(::sendGameUpdate)

    private fun sendGameUpdate(game: FightGame) {
        val content = Json.encodeToString(game).let(NotificationContent::Data)
        game.playerStates.keys.forEach { playerId ->
            notificationUseCase.send(playerId, WSCommand.FightGameUpdate.id, content)
        }
    }
}