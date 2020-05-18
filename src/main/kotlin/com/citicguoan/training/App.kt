package com.citicguoan.training

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.config.CorsRegistry

@SpringBootApplication
open class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
