package org.frchen.graphql.example.config

import graphql.kickstart.execution.context.DefaultGraphQLContext
import graphql.kickstart.execution.context.GraphQLContext
import graphql.servlet.context.DefaultGraphQLServletContext
import graphql.servlet.context.DefaultGraphQLWebSocketContext
import graphql.servlet.context.GraphQLServletContextBuilder
import org.dataloader.DataLoaderRegistry
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.websocket.Session
import javax.websocket.server.HandshakeRequest

@Component
abstract class DataLoaderContextBuilder : GraphQLServletContextBuilder {

    override fun build(request: HttpServletRequest, response: HttpServletResponse): GraphQLContext =
        DefaultGraphQLServletContext
            .createServletContext(dataLoaderRegistry(), null)
            .with(request)
            .with(response)
            .build()

    override fun build(session: Session, request: HandshakeRequest): GraphQLContext =
        DefaultGraphQLWebSocketContext
            .createWebSocketContext(dataLoaderRegistry(), null)
            .with(session)
            .with(request)
            .build();

    override fun build(): GraphQLContext =
        DefaultGraphQLContext(dataLoaderRegistry(), null)

    @Lookup
    protected abstract fun dataLoaderRegistry(): DataLoaderRegistry
}