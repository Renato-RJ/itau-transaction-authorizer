package com.itau.transaction_authorizer.adapter.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfiguration {
    @Bean
    fun objectMapper(): ObjectMapper =
        ObjectMapper()
            .setPropertyNamingStrategy(SNAKE_CASE)
            .registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
}
