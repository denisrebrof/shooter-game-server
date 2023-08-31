package com.denisrebrof.springboottest.hideandseekgame.domain

import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.hideandseekgame.domain.NotifyGameEventUseCase.RoundUpdateResponseSnapshot.Companion.toResponseSnapshot
import com.denisrebrof.springboottest.hideandseekgame.domain.core.GameState
import com.denisrebrof.springboottest.hideandseekgame.domain.core.HNSRoundFinishReason
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.HiderSnapshotItem
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.RoundEvent
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.RoundSnapshot
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.SeekerSnapshotItem
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.safeCast

@Service
class NotifyGameEventUseCase @Autowired constructor(
    private val notificationUseCase: SendUserNotificationUseCase,
    private val matchRepository: IMatchRepository
) {
    fun notify(event: RoundEvent, matchId: String) {
        val players = matchRepository.get(matchId)?.participantIds ?: return
        val content = event
            .toResponse()
            .let(Json::encodeToString)
            .let(NotificationContent::Data)
        notificationUseCase.send(players, WSCommand.RoundUpdate.id, content)
    }

    fun notify(state: GameState, matchId: String) {
        val players = matchRepository.get(matchId)?.participantIds ?: return
        val content = state.code.toString().let(NotificationContent::Data)
        notificationUseCase.send(players, WSCommand.GameState.id, content)
    }

    private fun RoundEvent.toResponse() = RoundUpdateResponse(
        snapshot = snapshot.toResponseSnapshot(),
        isFinished = this is RoundEvent.Finished,
        finishReason = RoundEvent.Finished::class.safeCast(this)?.reason
    )

    @Serializable
    private data class RoundUpdateResponse(
        val snapshot: RoundUpdateResponseSnapshot,
        val isFinished: Boolean,
        val finishReason: HNSRoundFinishReason?
    )

    @Serializable
    private data class RoundUpdateResponseSnapshot(
        val seekers: List<Pair<Long, SeekerSnapshotItem>>,
        val hiders: List<Pair<Long, HiderSnapshotItem>>
    ) {
        companion object {
            fun RoundSnapshot.toResponseSnapshot() = RoundUpdateResponseSnapshot(
                seekers.toList(),
                hiders.toList()
            )
        }
    }
}