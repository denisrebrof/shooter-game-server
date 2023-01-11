package com.denisrebrof.springboottest.userdata.model

import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "balances")
data class UserBalance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val currencyId: String,
    val userId: Long,
    val amount: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserBalance

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , currencyId = $currencyId , amount = $amount )"
    }
}