package com.denisrebrof.shooter.domain.game.delegates

import arrow.optics.copy
import arrow.optics.dsl.index
import arrow.optics.typeclasses.Index
import com.denisrebrof.shooter.domain.model.PlayingState
import com.denisrebrof.shooter.domain.model.players
import com.denisrebrof.shooter.domain.model.selectedWeaponId

fun PlayingState.selectWeapon(
    playerId: Long,
    weaponId: Long
) = copy {
    PlayingState
        .players
        .index(Index.map(), playerId)
        .selectedWeaponId set weaponId
}