package com.denisrebrof.springboottest.hideandseekgame.model

import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.HNSRoundFinishReason

sealed class RoundEvent {
    data class Update(val timeLeftMs: Long, val snapshot: RoundSnapshot) : RoundEvent()
    data class Finished(val reason: HNSRoundFinishReason, val snapshot: RoundSnapshot) : RoundEvent()
}

data class RoundSnapshot(
    val seekers: Map<Long, SeekerSnapshotItem>,
    val hiders: Map<Long, HiderSnapshotItem>
)

data class SeekerSnapshotItem(
    val transform: Transform,
    val character: Character,
    val catched: Int,
    val layed: Int,
    val catchedHiderId: Long?,
)

data class HiderSnapshotItem(
    val transform: Transform,
    val character: Character,
    val beenCatched: Int,
    val beenLayed: Int,
    val catcherId: Long?,
    val sleepPlaceId: Long?,
)