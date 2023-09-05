import gameentities.Transform

data class ShooterGameSettings(
    val redTeamSpawnPos: Transform = Transform(0f, 0f, 0f, 0f),
    val blueTeamSpawnPos: Transform = Transform(0f, 0f, 0f, 0f),
    val respawnDelay: Long = 3000L,
    val prepareDelay: Long = 10000L,
    val gameDuration: Long = 100000L,
    val completeDelay: Long = 10000L,
)