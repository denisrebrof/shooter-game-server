package com.denisrebrof.springboottest.lobby.gateways

import DisposableService
import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.lobby.domain.LobbyRepository
import com.denisrebrof.springboottest.lobby.domain.LobbyRepository.LobbyUpdate
import com.denisrebrof.springboottest.lobby.domain.LobbyRepository.LobbyUpdate.LobbyUpdateType
import com.denisrebrof.springboottest.lobby.domain.model.LobbyUserState
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import subscribeDefault

@Service
class LobbyUpdatesNotifier @Autowired constructor(
    private val sendUserNotificationUseCase: SendUserNotificationUseCase,
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
        sendUserNotificationUseCase.send(
            userId = update.userId,
            commandId = WSCommand.LobbyState.id,
            content = state.code.toString().let(NotificationContent::Data)
        )
    }
}