package com.citicguoan.training.bll.context

import com.citicguoan.training.bll.AuthorizationQuery
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component

@Component
open class AppContextFactory(
    private val authorizationQuery: AuthorizationQuery
): GraphQLContextFactory<AppContext> {

    override suspend fun generateContext(
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): AppContext =
        request
            .headers["Authorization"]
            ?.takeIf { it.isNotEmpty() }
            ?.get(0)
            ?.let {
                authorizationQuery.user(it)
            }
            .let {
                AppContext(it)
            }
}