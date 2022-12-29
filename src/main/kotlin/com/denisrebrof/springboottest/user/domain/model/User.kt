package com.denisrebrof.springboottest.user.domain.model

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    val username: String,
    val password: String = "",
    val role: UserRole = UserRole.Default,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val yandexId: String = ""
) {
    override fun toString(): String {
        return "User id=$id username=$username"
    }
}
