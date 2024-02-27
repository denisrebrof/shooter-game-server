package com.denisrebrof.shooter.domain.game.delegates

import arrow.optics.Copy
import com.denisrebrof.shooter.domain.model.PlayingState
import com.denisrebrof.shooter.domain.model.dynamicState
import com.denisrebrof.shooter.domain.model.playing
import com.denisrebrof.shooter.domain.model.selectedWeaponId

fun Copy<PlayingState>.shoot(
    current: PlayingState,
    shooterId: Long,
    weaponId: Long
): Boolean {
    val shooter = PlayingState.getPlayerStateOptional(shooterId)
    val shooterPlaying = shooter.dynamicState.playing
    if (shooterPlaying.isEmpty(current))
        return false

    shooter.selectedWeaponId set weaponId
    return true
}