package com.denisrebrof.sringboottest

import com.denisrebrof.sringboottest.messaging.ChatMessage
import com.denisrebrof.sringboottest.messaging.components.ChatMessageConfiguration
import com.denisrebrof.sringboottest.user.IUserRepository
import com.denisrebrof.sringboottest.user.model.User
import com.denisrebrof.sringboottest.user.model.UserRole
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

@SpringBootApplication
class SpringBootTestApplication(
    private val usersRepository: IUserRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        arrayOf(
            User(username = "denisrebrof", password = "asdfgasdf", role = UserRole.Admin),
        ).forEach(usersRepository::save)

        val context = AnnotationConfigApplicationContext(ChatMessageConfiguration::class.java)
        val userBean = context.getBean(ChatMessage::class.java)
        println(userBean)
    }
}

fun main(args: Array<String>) {
    runApplication<SpringBootTestApplication>(*args)
}