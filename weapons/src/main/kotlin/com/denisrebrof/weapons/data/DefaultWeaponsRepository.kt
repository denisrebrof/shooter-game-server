package com.denisrebrof.weapons.data

import com.denisrebrof.weapons.domain.model.WeaponInfo
import com.denisrebrof.weapons.domain.model.WeaponSettings
import com.denisrebrof.weapons.domain.repositories.IWeaponInfoRepository
import org.springframework.stereotype.Service

@Service
class DefaultWeaponsRepository : IWeaponInfoRepository {

    private val weaponsMap = mapOf(
        0L to WeaponInfo(
            id = 0L,
            name = "Beretta 57",
            nameLocalizationKey = "beretta_57",
            automatic = true,
            availableFromLevel = 0,
            premium = false,
            settingsLevels = listOf(
                WeaponSettings(
                    rpm = 600,
                    damage = 22,
                    rounds = 20,
                    cost = 10
                ),
            )
        ),

        1L to WeaponInfo(
            id = 1L,
            name = "Gewehr 43",
            nameLocalizationKey = "gewehr_43",
            automatic = false,
            availableFromLevel = 0,
            premium = false,
            settingsLevels = listOf(
                WeaponSettings(
                    rpm = 300,
                    damage = 35,
                    rounds = 10,
                    cost = 42
                ),
            )
        ),

        2L to WeaponInfo(
            id = 2L,
            name = "MP 40",
            nameLocalizationKey = "mp_40",
            automatic = true,
            availableFromLevel = 0,
            premium = false,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 500,
                    damage = 20,
                    rounds = 32,
                    cost = 30
                ),
            )
        ),

        3L to WeaponInfo(
            id = 3L,
            name = "PPSH 41",
            nameLocalizationKey = "ppsh_41",
            automatic = true,
            availableFromLevel = 0,
            premium = false,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 900,
                    damage = 20,
                    rounds = 71,
                    cost = 10
                ),
            )
        ),

        4L to WeaponInfo(
            id = 4L,
            name = "SKS",
            nameLocalizationKey = "sks",
            automatic = false,
            availableFromLevel = 0,
            premium = false,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 40,
                    damage = 40,
                    rounds = 10,
                    cost = 10
                ),
            )
        ),

        5L to WeaponInfo(
            id = 5L,
            name = "STG 44",
            nameLocalizationKey = "stg_44",
            automatic = true,
            availableFromLevel = 0,
            premium = false,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 500,
                    damage = 25,
                    rounds = 30,
                    cost = 10
                ),
            )
        ),

        6L to WeaponInfo(
            id = 6L,
            name = "Tokarev",
            nameLocalizationKey = "tokarev",
            automatic = false,
            availableFromLevel = 0,
            premium = false,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 600,
                    damage = 20,
                    rounds = 8,
                    cost = 10
                ),
            )
        ),

        7L to WeaponInfo(
            id = 7L,
            name = "Walter",
            nameLocalizationKey = "walter",
            automatic = false,
            availableFromLevel = 0,
            premium = false,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 480,
                    damage = 18,
                    rounds = 8,
                    cost = 10
                ),
            )
        ),

        8L to WeaponInfo(
            id = 8L,
            name = "Beretta 57 Gold",
            nameLocalizationKey = "beretta_57_gold",
            automatic = true,
            availableFromLevel = 0,
            premium = true,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 720,
                    damage = 26,
                    rounds = 24,
                    cost = 14
                ),
            )
        ),

        9L to WeaponInfo(
            id = 9L,
            name = "Gewehr 43 Gold",
            nameLocalizationKey = "gewehr_43_gold",
            automatic = false,
            availableFromLevel = 0,
            premium = true,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 360,
                    damage = 42,
                    rounds = 12,
                    cost = 42
                ),
            )
        ),

        10L to WeaponInfo(
            id = 10L,
            name = "MP 40 Gold",
            nameLocalizationKey = "mp_40_gold",
            automatic = true,
            availableFromLevel = 0,
            premium = true,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 600,
                    damage = 24,
                    rounds = 38,
                    cost = 30
                ),
            )
        ),

        11L to WeaponInfo(
            id = 11L,
            name = "PPSH 41 Gold",
            nameLocalizationKey = "ppsh_41_gold",
            automatic = true,
            availableFromLevel = 0,
            premium = true,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 1080,
                    damage = 24,
                    rounds = 85,
                    cost = 10
                ),
            )
        ),

        12L to WeaponInfo(
            id = 12L,
            name = "SKS Gold",
            nameLocalizationKey = "sks_gold",
            automatic = false,
            availableFromLevel = 0,
            premium = true,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 48,
                    damage = 48,
                    rounds = 12,
                    cost = 10
                ),
            )
        ),

        13L to WeaponInfo(
            id = 13L,
            name = "STG 44 Gold",
            nameLocalizationKey = "stg_44_gold",
            automatic = true,
            availableFromLevel = 0,
            premium = true,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 600,
                    damage = 30,
                    rounds = 36,
                    cost = 10
                ),
            )
        ),

        14L to WeaponInfo(
            id = 14L,
            name = "Tokarev Gold",
            nameLocalizationKey = "tokarev_gold",
            automatic = false,
            availableFromLevel = 0,
            premium = true,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 720,
                    damage = 24,
                    rounds = 10,
                    cost = 10
                ),
            )
        ),

        15L to WeaponInfo(
            id = 15L,
            name = "Walter Gold",
            nameLocalizationKey = "walter_gold",
            automatic = false,
            availableFromLevel = 0,
            premium = true,
                settingsLevels = listOf(
                WeaponSettings(
                    rpm = 576,
                    damage = 21,
                    rounds = 10,
                    cost = 10
                ),
            )
        )
    )

    private val weaponsList = weaponsMap.values.toList()

    override fun get(id: Long): WeaponInfo? = weaponsMap[id]

    override fun getAll(): List<WeaponInfo> = weaponsList

}