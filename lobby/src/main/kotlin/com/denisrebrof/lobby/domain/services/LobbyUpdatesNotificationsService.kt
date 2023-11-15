package com.denisrebrof.lobby.domain.services

import com.denisrebrof.commands.domain.model.NotificationContent
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.lobby.domain.LobbyRepository
import com.denisrebrof.lobby.domain.LobbyRepository.LobbyUpdate
import com.denisrebrof.lobby.domain.LobbyRepository.LobbyUpdate.LobbyUpdateType
import com.denisrebrof.lobby.domain.model.LobbyUserState
import com.denisrebrof.user.domain.SendUserNotificationUseCase
import com.denisrebrof.utils.DisposableService
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LobbyUpdatesNotificationsService @Autowired constructor(
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