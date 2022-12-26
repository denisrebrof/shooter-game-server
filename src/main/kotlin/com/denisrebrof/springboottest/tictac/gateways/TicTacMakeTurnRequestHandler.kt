package com.denisrebrof.springboottest.tictac.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommandId
import com.denisrebrof.springboottest.commands.gateways.WSRequestHandler
import com.denisrebrof.springboottest.tictac.domain.TicTacMakeTurnUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicTacMakeTurnRequestHandler @Autowired constructor(
    private val turnUseCase: TicTacMakeTurnUseCase
) : WSRequestHandler<Int>(WSCommandId.TicTacMakeTurn.id) {

    override fun parseData(data: String): Int = data.toInt()

    override fun handleMessage(userId: Long, data: Int): ResponseState = turnUseCase.makeTurn(userId, data).toResponse()

    private fun Boolean.toResponse() = this.toString().let(ResponseState::CreatedResponse)
}