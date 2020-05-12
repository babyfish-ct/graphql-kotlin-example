package com.citicguoan.training

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
