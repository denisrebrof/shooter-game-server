package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.fight.domain.FightGamesRepository
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository.GameUpdateType
import com.denisrebrof.springboottest.fight.domain.model.GameState
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StartGameHandler @Autowired constructor(
    private val gamesRepository: FightGamesRepository
) : DisposableService() {

    companion object {
        private const val GAME_START_DELAY: Long = 3000L
    }

    override val handler: Disposable
        get() = gamesRepository
            .getUpdates()
            .filter { it.type == GameUpdateType.Created }
            .map(FightGamesRepository.GameUpdate::matchId)
            .delay(GAME_START_DELAY, TimeUnit.MILLISECONDS)
            .subscribeDefault(::startMatchIfReady)

    private fun startMatchIfReady(matchId: String) {
        val currentState = gamesRepository.get(matchId) ?: return
        if (currentState.state != GameState.Preparing)
            return

        val newGameState = currentState.copy(state = GameState.Playing)
        gamesRepository.set(matchId, newGameState)
    }
}