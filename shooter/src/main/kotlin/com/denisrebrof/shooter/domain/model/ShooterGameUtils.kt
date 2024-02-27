package com.denisrebrof.shooter.domain.model

fun ShooterGameIntents.Hit.toAction(
    killed: Boolean,
    hpLoss: Int,
) = ShooterGameActions.Hit(
    damagerId = shooterId,
    receiverId = receiverId,
    hpLoss = hpLoss,
    killed = killed
)