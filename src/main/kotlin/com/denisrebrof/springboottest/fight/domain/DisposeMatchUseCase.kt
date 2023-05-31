package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class DisposeMatchUseCase @Autowired constructor(
    private val matchRepository: IMatchRepository
) : DisposableBean {
    private val removeMatchesHandler = CompositeDisposable()
    private val removedMatchIds = mutableSetOf<String>()

    fun disposeMatch(matchId: String) {
        if (removedMatchIds.contains(matchId))
            return

        startMatchDisposeTimer(matchId)
            .subscribeDefault(::dispose)
            .let(removeMatchesHandler::add)
    }

    private fun startMatchDisposeTimer(matchId: String): Maybe<String> = Maybe
        .timer(matchDisposeDelay, TimeUnit.MILLISECONDS)
        .map { matchId }

    private fun dispose(matchId: String) {
        matchRepository.remove(matchId)
        removedMatchIds.remove(matchId)
    }

    override fun destroy() = removeMatchesHandler.clear()

    companion object {
        private const val matchDisposeDelay = 10 * 1000L
    }
}