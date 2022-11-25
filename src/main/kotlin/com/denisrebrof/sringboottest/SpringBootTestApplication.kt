package com.denisrebrof.sringboottest

import com.denisrebrof.sringboottest.data.IUserRepository
import com.denisrebrof.sringboottest.data.model.User
import com.denisrebrof.sringboottest.data.model.UserRole
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootTestApplication(
    private val usersRepository: IUserRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val testUser = User(
            username = "denisrebrof",
            password = "asdfgasdf",
            role = UserRole.Admin
        )
        usersRepository.save(testUser)
        usersRepository.findAll().forEach<User?>(System.out::println)
    }
}

fun main(args: Array<String>) {
    runApplication<SpringBootTestApplication>(*args)
}