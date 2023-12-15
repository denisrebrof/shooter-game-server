package com.denisrebrof.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { registry ->
            registry.requestMatchers("/websocket").permitAll()
            registry.anyRequest().authenticated()
        }
        .anonymous().disable()
        .httpBasic()
        .and()
        .cors().disable()
        .csrf().disable()
        .build()
}