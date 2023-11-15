package com.denisrebrof.user.domain.model

data class User(
    val id: Long = 0L,
    val username: String = "",
    val password: String = "",
    val role: UserRole = UserRole.Default,
    val identities: Map<UserIdentityType, String> = mapOf()
)

data class UserIdentity(
    val id: String,
    val type: UserIdentityType
) {
    companion object {
        fun fromUserId(userId: Long): UserIdentity {
            val id = userId.toString()
            return UserIdentity(id, UserIdentityType.Id)
        }
    }
}

enum class UserIdentityType {
    Id,
    LocalId,
    YandexId,
    Token,
    Username
}

enum class UserRole {
    Default,
    Admin
}
