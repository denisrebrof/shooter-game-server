package com.denisrebrof.userdata.repositories

import com.denisrebrof.userdata.model.UserData
import com.denisrebrof.userdata.model.UserDataMapper.toUser
import com.denisrebrof.userdata.internal.UserDataRepository
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.model.UserIdentityType
import com.denisrebrof.user.domain.repositories.ICreateUserRepository
import com.denisrebrof.user.domain.repositories.ICreateUserRepository.CreateUserResult.Failure
import com.denisrebrof.user.domain.repositories.ICreateUserRepository.CreateUserResult.Failure.Reason
import com.denisrebrof.user.domain.repositories.IUserRepository
import com.denisrebrof.userdata.UserNameFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CreateUserRepositoryImpl @Autowired constructor(
    private val userRepository: IUserRepository,
    private val userDataRepository: UserDataRepository,
) : ICreateUserRepository {

    override fun createUser(identity: UserIdentity): ICreateUserRepository.CreateUserResult {
        if (identity.id.isBlank())
            return Failure(Reason.IncorrectIdentity)

        if (userRepository.find(identity) != null)
            return Failure(Reason.IdentityCollision)

        val newUserData = UserData(
            username = UserNameFactory.createNewNick(),
            yandexId = if (identity.type == UserIdentityType.YandexId) identity.id else "",
            localId = if (identity.type == UserIdentityType.LocalId) identity.id else ""
        )
        return userDataRepository
            .save(newUserData)
            .toUser()
            .let(ICreateUserRepository.CreateUserResult::Success)
    }
}