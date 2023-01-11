package com.denisrebrof.springboottest.userdata.repositories

import com.denisrebrof.springboottest.user.domain.model.UserIdentity
import com.denisrebrof.springboottest.user.domain.model.UserIdentityType
import com.denisrebrof.springboottest.user.domain.repositories.ICreateUserRepository
import com.denisrebrof.springboottest.user.domain.repositories.ICreateUserRepository.CreateUserResult
import com.denisrebrof.springboottest.user.domain.repositories.ICreateUserRepository.CreateUserResult.Failure.Reason
import com.denisrebrof.springboottest.user.domain.repositories.IUserRepository
import com.denisrebrof.springboottest.userdata.model.UserData
import com.denisrebrof.springboottest.userdata.model.UserDataMapper.toUser
import com.denisrebrof.springboottest.userdata.repositories.internal.UserDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CreateUserRepositoryImpl @Autowired constructor(
    private val userRepository: IUserRepository,
    private val userDataRepository: UserDataRepository,
) : ICreateUserRepository {
    override fun createUser(identity: UserIdentity): CreateUserResult {
        if (identity.id.isBlank())
            return CreateUserResult.Failure(Reason.IncorrectIdentity)

        if (userRepository.find(identity) != null)
            return CreateUserResult.Failure(Reason.IdentityCollision)

        val newUserData = UserData(
            username = "New User",
            yandexId = if (identity.type == UserIdentityType.YandexId) identity.id else "",
            localId = if (identity.type == UserIdentityType.LocalId) identity.id else ""
        )
        return userDataRepository
            .save(newUserData)
            .toUser()
            .let(CreateUserResult::Success)
    }
}