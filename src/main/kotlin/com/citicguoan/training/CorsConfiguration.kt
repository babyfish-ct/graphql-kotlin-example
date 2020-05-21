package com.citicguoan.training

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
internal open class CorsConfiguration {

    @Bean
    open fun corsFilter(): CorsWebFilter = CorsWebFilter(
        UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(
                "/graphql",
                CorsConfiguration().apply {
                    allowCredentials = true
                    allowedOrigins = listOf("*")
                    allowedHeaders = listOf("*")
                    allowedMethods = listOf("*")
                }
            )
        }
    )
}