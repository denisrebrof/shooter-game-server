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

    override fun addIdentity(userId: Long, identity: UserIdentity): Boolean {
        var data = repository.findUserDataById(userId)?: return false
        data = when(identity.type) {
            UserIdentityType.Id -> return false
            UserIdentityType.LocalId -> return false
            UserIdentityType.YandexId -> if(data.yandexId.isEmpty()) data.copy(yandexId = identity.id) else return false
            UserIdentityType.Token -> return false
            UserIdentityType.Username -> return false
        }
        repository.save(data)
        return true
    }

    private fun findData(identity: UserIdentity): UserData? = when (identity.type) {
        UserIdentityType.Id -> identity.id.toLongOrNull()?.let(repository::findUserDataById)
        UserIdentityType.Username -> repository.findUserDataByUsername(identity.id).firstOrNull()
        UserIdentityType.LocalId -> repository.findUserDataByLocalId(identity.id)
        UserIdentityType.YandexId -> repository.findUserDataByYandexId(identity.id)
        UserIdentityType.Token -> null
    }
}