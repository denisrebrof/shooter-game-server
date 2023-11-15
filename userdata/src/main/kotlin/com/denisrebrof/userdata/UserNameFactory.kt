package com.denisrebrof.userdata

import java.util.concurrent.ThreadLocalRandom

object UserNameFactory {

    private fun getRandomIndex(max: Int): Int = ThreadLocalRandom.current().nextInt(max) % max

    private val usernamePrefixes = listOf(
        "Deadly",
        "Brave",
        "Strong",
        "Fast",
        "Real",
        "Bouncy",
        "Fiery",
        "Frosty",
        "Stone",
        "Naughty",
    )

    private val usernamePostfixes = listOf(
        "Astronaut",
        "Scout",
        "Sniper",
        "Knight",
        "Signalman",
        "Paratrooper",
        "Bully",
        "Peacemaker",
        "Raider",
        "Barbarian",
    )

    fun createNewNick(): String {
        val prefix = getRandomIndex(usernamePrefixes.size).let(usernamePrefixes::get)
        val postfix = getRandomIndex(usernamePostfixes.size).let(usernamePostfixes::get)
        val number = getRandomIndex(9999)
        return "${prefix}_${postfix}_$number"
    }
}