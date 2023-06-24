package com.denisrebrof.springboottest.hideandseekgame.domain.core.model

import com.denisrebrof.springboottest.game.domain.model.Transform

data class Role(
    val character: Character,
    val initialPos: Transform,
    val isSeeker: Boolean
) {
    val isHider = !isSeeker
}