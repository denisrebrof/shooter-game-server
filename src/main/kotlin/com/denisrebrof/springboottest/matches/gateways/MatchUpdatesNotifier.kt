package com.denisrebrof.springboottest.matches.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommandId
import com.denisrebrof.springboottest.commands.gateways.WSNotificationService
import com.denisrebrof.springboottest.matches.domain.MatchRepository
import com.denisrebrof.springboottest.matches.domain.MatchRepository.MatchUpdate
import com.denisrebrof.springboottest.matches.domain.MatchRepository.MatchUpdate.UpdateType
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MatchUpdatesNotifier @Autowired constructor(
    private val notificationService: WSNotificationService,
    matchRepository: MatchRepository
) : DisposableService() {

    private val emptyMatch = Match(
        id = "",
        createdTime = 0L,
        participantIds = listOf()
    )

    override val handler: Disposable = matchRepository
        .getMatchUpdates()
        .onBackpressureBuffer()
        .subscribeDefault(::handle)

    private fun handle(update: MatchUpdate) {
        val messageData = when (update.type) {
            UpdateType.Created -> update.match
            UpdateType.Removed -> emptyMatch
        }.let(Json::encodeToString)
        update.match.participantIds.forEach { userId ->
            notificationService.send(userId, WSCommandId.GetMatch.id, messageData)
        }
    }
}