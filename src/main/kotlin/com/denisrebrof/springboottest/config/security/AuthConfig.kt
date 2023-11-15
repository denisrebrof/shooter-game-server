package com.denisrebrof.springboottest.config.security

import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.model.UserIdentityType
import com.denisrebrof.user.domain.repositories.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthConfig @Autowired constructor(
    private val userRepository: IUserRepository
) {
    @Bean
    fun userDetailsService() = UserDetailsService { username ->
        username ?: throw UsernameNotFoundException("username is null")

        val user = UserIdentity(username, UserIdentityType.Username)
            .let(userRepository::find)
            ?: throw UsernameNotFoundException("User with username $username not found")
        User
            .withUsername(user.username)
            .password(user.password)
            .roles("USER")
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}