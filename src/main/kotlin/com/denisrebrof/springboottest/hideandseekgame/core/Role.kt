package com.denisrebrof.springboottest.hideandseekgame.core

data class Role(
    val character: Character,
    val initialPos: Transform,
    val isSeeker: Boolean
) {
    val isHider = !isSeeker
}