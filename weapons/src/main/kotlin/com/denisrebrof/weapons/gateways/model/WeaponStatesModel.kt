package com.denisrebrof.weapons.gateways.model

import com.denisrebrof.weapons.domain.model.WeaponInfo
import kotlinx.serialization.Serializable


@Serializable
data class WeaponStatesResponseData(
    val weapons: List<WeaponStateResponseData>,
    val primaryId: Long,
    val secondaryId: Long,
)

@Serializable
data class WeaponStateResponseData(
    val info: WeaponInfo,
    val currentLevel: Int
)