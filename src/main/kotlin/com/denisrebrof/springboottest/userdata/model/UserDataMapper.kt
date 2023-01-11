package com.denisrebrof.springboottest.userdata.model

import com.denisrebrof.springboottest.user.domain.model.User
import com.denisrebrof.springboottest.user.domain.model.UserIdentityType

object UserDataMapper {
    fun UserData.toUser() = User(
        id ?: 0L,
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