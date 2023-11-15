package com.denisrebrof.userdata.model

import com.denisrebrof.user.domain.model.User
import com.denisrebrof.user.domain.model.UserIdentityType

object UserDataMapper {
    fun UserData.toUser() = User(
        id,
        username,
        password,
        role,
        getIdentities(this)
    )

    private fun getIdentities(data: UserData): Map<UserIdentityType, String> {
        val identities = mutableMapOf(UserIdentityType.Id to data.id.toString())
        val potentialIdentities = mapOf(
            UserIdentityType.YandexId to data.yandexId,
            UserIdentityType.LocalId to data.localId,
        )
        potentialIdentities
            .filterValues(String::isNotBlank)
            .let(identities::putAll)
        return identities
    }
}