package com.denisrebrof.springboottest.commands.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState

abstract class WSSessionEmptyRequestHandler(override val id: Long) : WSSessionRequestHandler<Unit>(id) {
    override fun parseData(data: String) = Unit
    final override fun handleMessage(sessionId: String, data: Unit): ResponseState = handleMessage(sessionId)
    abstract fun handleMessage(sessionId: String): ResponseState
}