package com.denisrebrof.springboottest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import javax.servlet.ServletContext

@SpringBootApplication
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

