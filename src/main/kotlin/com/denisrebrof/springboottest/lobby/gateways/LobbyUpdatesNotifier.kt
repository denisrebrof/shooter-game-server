package com.denisrebrof.springboottest.lobby.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommandId
import com.denisrebrof.springboottest.commands.gateways.WSNotificationService
import com.denisrebrof.springboottest.lobby.domain.LobbyRepository
import com.denisrebrof.springboottest.lobby.domain.LobbyRepository.LobbyUpdate
import com.denisrebrof.springboottest.lobby.domain.LobbyRepository.LobbyUpdate.LobbyUpdateType
import com.denisrebrof.springboottest.lobby.domain.model.LobbyUserState
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LobbyUpdatesNotifier @Autowired constructor(
    private val notificationService: WSNotificationService,
    lobbyRepository: LobbyRepository
) : DisposableService() {

    override val handler: Disposable = lobbyRepository
        .getUpdates()
        .subscribeDefault(::handleLobbyUpdate)

    private fun handleLobbyUpdate(update: LobbyUpdate) {
        val state = when (update.type) {
            LobbyUpdateType.Join -> LobbyUserState.Joined
            LobbyUpdateType.Left -> LobbyUserState.NotIn
        }
        notificationService.send(
            update.userId,
            WSCommandId.LobbyState.id,
            state.code.toString()
        )
    }
}