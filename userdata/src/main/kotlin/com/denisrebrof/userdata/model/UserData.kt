package com.denisrebrof.userdata.model

import com.denisrebrof.user.domain.model.UserRole
import org.hibernate.Hibernate
import java.util.*
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
    val rating: Int = 0,
    val kills: Int = 0,
    val death: Int = 0,
    val level: Int = 1,
    val lastClaimedLevel: Int = 1,
    val xp: Int = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val loginCount: Int = 0,
//    @Column(name = "account_creation_time", nullable = true)
//    val accountCreationTime: Date? = Date(),
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
}

