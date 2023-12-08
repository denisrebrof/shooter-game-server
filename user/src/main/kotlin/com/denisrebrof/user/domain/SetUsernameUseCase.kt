package com.denisrebrof.user.domain

import com.denisrebrof.commands.domain.model.NotificationContent
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.domain.model.SetUsernameResult
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.model.UserIdentityType
import com.denisrebrof.user.domain.repositories.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SetUsernameUseCase @Autowired constructor(
    private val userRepository: IUserRepository,
    private val sendUserNotificationUseCase: SendUserNotificationUseCase
) {
    private val nickRegex = Regex("^[a-zA-Z0-9]+([._]?[a-zA-Z0-9]+)+$")

    fun setUsername(userId: Long, newNick: String): SetUsernameResult {
        val identity = UserIdentity.fromUserId(userId)
        userRepository.find(identity) ?: return SetUsernameResult.Error

        val existingUserWithNickname = UserIdentity(newNick, UserIdentityType.Username).let(userRepository::find)
        if (existingUserWithNickname != null && existingUserWithNickname.id != userId)
            return SetUsernameResult.NotAvailable

        if (!checkValidUsername(newNick))
            return SetUsernameResult.NotAvailable

        val updateSucceed = runCatching {
            userRepository.updateNick(identity, newNick)
        }.getOrNull() ?: false

        if (updateSucceed) {
            val content = NotificationContent.Data(newNick)
            sendUserNotificationUseCase.send(userId, WSCommand.GetUserName.id, content)
        }

        return when {
            updateSucceed -> SetUsernameResult.Success
            else -> SetUsernameResult.Error
        }
    }

    private fun checkValidUsername(nick: String): Boolean {
        if (nick.length !in 8..20)
            return false

        return nick.matches(nickRegex)
    }
}