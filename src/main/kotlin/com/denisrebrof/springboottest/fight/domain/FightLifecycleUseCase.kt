package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.FightGamesRepository.GameUpdateType
import com.denisrebrof.springboottest.fight.domain.model.GameState
import com.denisrebrof.springboottest.fight.domain.model.PlayerState
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class FightLifecycleUseCase @Autowired constructor(
    private val matchRepository: IMatchRepository,
    private val gamesRepository: FightGamesRepository
) : DisposableBean {
    private val fightStateDisposables = Collections.synchronizedMap(mutableMapOf<String, CompositeDisposable>())

    private val fightStatesDisposeHandler = gamesRepository
        .getUpdates()
        .filter { update -> update.type == GameUpdateType.Removed || update.game.state != GameState.Playing }
        .map(FightGamesRepository.GameUpdate::matchId)
        .subscribeDefault(::dispose)

    fun doInFightOrSkip(userId: Long, action: (PlayerState) -> Disposable) {
        val matchId = matchRepository.getMatchIdByUserId(userId) ?: return
        val game = gamesRepository.get(matchId) ?: return
        if (game.state != GameState.Playing)
            return

        val state = game.playerStates[userId] ?: return
        fightStateDisposables
            .getOrPut(matchId, ::CompositeDisposable)
            .add(action(state))
    }

    private fun dispose(matchId: String) {
        fightStateDisposables[matchId]?.dispose()
        fightStateDisposables.remove(matchId)
    }

    override fun destroy() = fightStatesDisposeHandler.dispose()
}