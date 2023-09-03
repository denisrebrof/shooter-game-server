package com.denisrebrof.springboottest.matches.gateways

import DisposableService
import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate.UpdateType
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import subscribeDefault

@Service
class MatchUpdatesNotifier @Autowired constructor(
    private val sendUserNotificationUseCase: SendUserNotificationUseCase,
    matchRepository: IMatchRepository
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
        val messageContent = when (update.type) {
            UpdateType.Created -> update.match
            UpdateType.Removed -> emptyMatch
        }.let(Json::encodeToString).let(NotificationContent::Data)
        update.match.participantIds.forEach { userId ->
            sendUserNotificationUseCase.send(userId, WSCommand.GetMatch.id, messageContent)
        }
    }
}