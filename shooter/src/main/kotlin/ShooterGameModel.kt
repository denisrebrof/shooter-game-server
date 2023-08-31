import arrow.optics.optics

enum class PlayerTeam(val id: Int) {
    Undefined(0),
    Red(1),
    Blue(2)
}

data class ShooterPlayerState(
    val data: ShooterPlayerData,
    val dynamicState: ShooterDynamicState = ShooterDynamicState.Pending
)

sealed class ShooterDynamicState {
    object Pending : ShooterDynamicState()

    data class Playing(
        val transform: Transform,
        val verticalLookAngle: Float,
        val selectedWeaponId: Long
    ) : ShooterDynamicState()
}

data class ShooterPlayerData(
    val userId: Long,
    val team: PlayerTeam,
    val kills: Int = 0,
    val death: Int = 0
)

data class ShooterGameState(
    val playerStates: Map<Long, ShooterPlayerState>
)