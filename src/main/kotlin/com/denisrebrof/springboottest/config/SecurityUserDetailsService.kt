package com.denisrebrof.springboottest.config

import com.denisrebrof.springboottest.user.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class SecurityUserDetailsService @Autowired constructor(
    private val userRepository: IUserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        username ?: throw UsernameNotFoundException("username is null")
        val user = userRepository
            .findUserByUsername(username)
            .firstOrNull()
            ?: throw UsernameNotFoundException("User with username $username not found")
        return User
            .withUsername(user.username)
            .password(user.password)
            .roles("USER")
            .build()
    }
}