package com.denisrebrof.springboottest.hideandseekgame.domain.core.model

import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.domain.core.HNSRoundFinishReason
import kotlinx.serialization.Serializable

sealed class RoundEvent(open val snapshot: RoundSnapshot) {
    data class Update(
        val timeLeftMs: Long,
        override val snapshot: RoundSnapshot
    ) : RoundEvent(snapshot)

    data class Finished(
        val reason: HNSRoundFinishReason,
        override val snapshot: RoundSnapshot
    ) : RoundEvent(snapshot)
}

@Serializable
data class RoundSnapshot(
    val seekers: Map<Long, SeekerSnapshotItem>,
    val hiders: Map<Long, HiderSnapshotItem>
)

@Serializable
data class SeekerSnapshotItem(
    val transform: Transform,
    val character: Character,
    val catched: Int,
    val layed: Int,
    val catchedHiderId: Long?,
)

@Serializable
data class HiderSnapshotItem(
    val transform: Transform,
    val character: Character,
    val beenCatched: Int,
    val beenLayed: Int,
    val catcherId: Long?,
    val sleepPlaceId: Long?,
)