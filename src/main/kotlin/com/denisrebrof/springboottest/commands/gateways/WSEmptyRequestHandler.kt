package com.denisrebrof.springboottest.commands.gateways

abstract class WSEmptyRequestHandler(override val id: Long) : WSRequestHandler<Unit>(id) {
    override fun parseData(data: String) = Unit
    override fun handleMessage(userId: Long, data: Unit): ResponseState = handleMessage(userId)
    abstract fun handleMessage(userId: Long): ResponseState
}