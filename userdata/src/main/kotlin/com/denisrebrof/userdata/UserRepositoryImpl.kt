package com.denisrebrof.userdata

import com.denisrebrof.userdata.model.UserDataMapper.toUser
import com.denisrebrof.userdata.internal.UserDataRepository
import com.denisrebrof.user.domain.model.User
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.model.UserIdentityType
import com.denisrebrof.user.domain.repositories.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserRepositoryImpl @Autowired constructor(
    private val repository: UserDataRepository
) : IUserRepository {

    override fun find(identity: UserIdentity): User? = when (identity.type) {
        UserIdentityType.Id -> identity.id.toLongOrNull()?.let(repository::findUserDataById)
        UserIdentityType.Username -> repository.findUserDataByUsername(identity.id).firstOrNull()
        UserIdentityType.LocalId -> repository.findUserDataByLocalId(identity.id)
        UserIdentityType.YandexId -> repository.findUserDataByYandexId(identity.id)
        UserIdentityType.Token -> null
    }?.toUser()
}