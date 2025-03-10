package com.denisrebrof.weapons.domain.model

import kotlinx.serialization.Serializable

object DefaultWeaponIds {
    val slotToIds = mapOf(
        WeaponSlot.Primary to 0L,
        WeaponSlot.Secondary to 7L,
    )

    val Ids = slotToIds.values.toSet()
}

enum class WeaponSlot {
    Primary,
    Secondary
}

data class PlayerWeaponInfo(
    val weaponId: Long,
    val weaponLevel: Int = 1
)

@Serializable
data class PlayerWeapon(
    val id: Long,
    val name: String,
    val nameLocalizationKey: String,
    val level: Int,
    val settings: WeaponSettings
) {
    companion object {
        fun WeaponInfo.toPlayerWeapon(level: Int) = settingsLevels.getOrNull(level - 1)?.let { settings ->
            PlayerWeapon(id, name, nameLocalizationKey, level, settings)
        }

        val Undefined = PlayerWeapon(
            id = -1L,
            name = "Undefined",
            nameLocalizationKey = "",
            level = 0,
            settings = WeaponSettings(0, 0, 0, 0)
        )
    }
}

@Serializable
data class WeaponInfo(
    val id: Long,
    val name: String,
    val nameLocalizationKey: String,
    val availableFromLevel: Int,
    val automatic: Boolean,
    val premium: Boolean,
    val settingsLevels: List<WeaponSettings>
)

@Serializable
data class WeaponSettings(
    val rpm: Int,
    val damage: Int,
    val rounds: Int,
    val cost: Long
)