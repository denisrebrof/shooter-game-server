package com.denisrebrof.sringboottest.data.model

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    val username: String,
    val password: String,
    val role: UserRole,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {
    override fun toString(): String {
        return "User id=$id username=$username"
    }
}
