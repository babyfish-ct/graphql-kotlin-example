package org.frchen.graphql.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class GraphQLApp

fun main(args: Array<String>) {
    runApplication<GraphQLApp>(*args)
}