package com.denisrebrof.matches.domain.services

import com.denisrebrof.commands.domain.model.NotificationContent
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.fromBoolean
import com.denisrebrof.matches.domain.model.Match
import com.denisrebrof.user.domain.SendUserNotificationUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MatchStateNotificationsService @Autowired constructor(
    private val sendUserNotificationUseCase: SendUserNotificationUseCase
) : IMatchServiceListener {

    private val Match.participantsParams
        get() = participants.toLongArray()

    override fun onMatchStarted(match: Match) = notifyStateChanged(true, *match.participantsParams)
    override fun onMatchFinished(match: Match) = notifyStateChanged(false, *match.participantsParams)
    override fun onJoined(match: Match, vararg participantIds: Long) = notifyStateChanged(true, *participantIds)
    override fun onLeft(match: Match, vararg participantIds: Long) = notifyStateChanged(false, *participantIds)

    private fun notifyStateChanged(state: Boolean, vararg receiverIds: Long) {
        val content = NotificationContent.fromBoolean(state)
        receiverIds.forEach { userId ->
            sendUserNotificationUseCase.send(userId, WSCommand.GetMatch.id, content)
        }
    }
}