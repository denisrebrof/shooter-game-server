package com.denisrebrof.springboottest.userdata.repositories

import com.denisrebrof.springboottest.user.domain.model.User
import com.denisrebrof.springboottest.user.domain.model.UserIdentity
import com.denisrebrof.springboottest.user.domain.model.UserIdentityType
import com.denisrebrof.springboottest.user.domain.repositories.IUserRepository
import com.denisrebrof.springboottest.userdata.model.UserDataMapper.toUser
import com.denisrebrof.springboottest.userdata.repositories.internal.UserDataRepository
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