package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseState

abstract class WSUserEmptyRequestHandler(
    override val id: Long
) : WSUserRequestHandler<Unit>(id) {
    override fun parseData(data: String) = Unit
    final override fun handleMessage(userId: Long, data: Unit): ResponseState = handleMessage(userId)
    abstract fun handleMessage(userId: Long): ResponseState
}