package com.denisrebrof.springboottest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.servlet.ServletContext

@SpringBootApplication
@ComponentScan(basePackages = ["com.denisrebrof"])
@EnableJpaRepositories(basePackages = ["com.denisrebrof"])
@EntityScan("com.denisrebrof.*")
class SpringBootTestApplication : SpringBootServletInitializer() {

    override fun onStartup(servletContext: ServletContext) {
        super.onStartup(servletContext)
        println("Debug startup")
    }

    override fun configure(builder: SpringApplicationBuilder?): SpringApplicationBuilder = builder!!
        .sources(SpringBootTestApplication::class.java)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<SpringBootTestApplication>(*args)
        }
    }
}

