package com.denisrebrof.userdata

import com.denisrebrof.user.domain.model.User
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.model.UserIdentityType
import com.denisrebrof.user.domain.repositories.IUserRepository
import com.denisrebrof.userdata.internal.UserDataRepository
import com.denisrebrof.userdata.model.UserData
import com.denisrebrof.userdata.model.UserDataMapper.toUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserRepositoryImpl @Autowired constructor(
    private val repository: UserDataRepository
) : IUserRepository {

    override fun updateNick(identity: UserIdentity, newNick: String): Boolean {
        var data = findData(identity) ?: return false
        data = data.copy(username = newNick)
        repository.save(data)
        return true
    }

    override fun increaseLoginCount(identity: UserIdentity): Boolean {
        var data = findData(identity) ?: return false
        data = data.copy(loginCount = data.loginCount + 1)
        repository.save(data)
        return true
    }

    override fun find(identity: UserIdentity): User? = findData(identity)?.toUser()

    private fun findData(identity: UserIdentity): UserData? = when (identity.type) {
        UserIdentityType.Id -> identity.id.toLongOrNull()?.let(repository::findUserDataById)
        UserIdentityType.Username -> repository.findUserDataByUsername(identity.id).firstOrNull()
        UserIdentityType.LocalId -> repository.findUserDataByLocalId(identity.id)
        UserIdentityType.YandexId -> repository.findUserDataByYandexId(identity.id)
        UserIdentityType.Token -> null
    }
}