package com.denisrebrof.userdata.model

import com.denisrebrof.user.domain.model.UserRole
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "users")
data class UserData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val username: String = "",
    val password: String = "",
    val role: UserRole = UserRole.Default,
    val yandexId: String = "",
    val localId: String = "",
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "owner_id")
    val balances: List<UserBalance> = listOf()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserData

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , username = $username , password = $password , role = $role , yandexId = $yandexId , localId = $localId )"
    }
}

