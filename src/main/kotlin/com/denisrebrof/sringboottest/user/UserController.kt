package com.denisrebrof.sringboottest.user

import com.denisrebrof.sringboottest.user.model.User
import com.denisrebrof.sringboottest.user.model.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class UserController @Autowired constructor(
    private val userRepository: IUserRepository,
) {
    @GetMapping("/registration/{username}")
    fun register(@PathVariable username: String): ResponseEntity<Void> {
        println("Register user $username")
        if (userRepository.countUsersByUsername(username) > 0) {
            println("User with such name exists, abort")
            return ResponseEntity.badRequest().build()
        }

        val user = User(username = username, password = "", role = UserRole.Default)
        userRepository.save(user)
        println("Saved user $username")
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getUsers")
    fun getUserNames(): Set<User> = userRepository
        .findAll()
        .toSet()
}