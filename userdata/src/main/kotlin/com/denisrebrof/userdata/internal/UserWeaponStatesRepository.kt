package com.denisrebrof.userdata.internal

import com.denisrebrof.userdata.model.UserWeaponState
import org.springframework.data.jpa.repository.JpaRepository

interface UserWeaponStatesRepository : JpaRepository<UserWeaponState, Long> {
    fun findByUserIdAndWeaponId(userId: Long, weaponId: Long): UserWeaponState?
    fun findByUserId(userId: Long): List<UserWeaponState>
}