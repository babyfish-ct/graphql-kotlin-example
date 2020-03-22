package org.frchen.graphql.example.config

import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode

@Configuration
open class DataLoaderRegistryConfiguration {

    @Scope(
        ConfigurableBeanFactory.SCOPE_PROTOTYPE,
        proxyMode = ScopedProxyMode.NO
    )
    @Bean
    open fun dataLoaderRegistry(
        loaders: List<DataLoader<*, *>>
    ): DataLoaderRegistry =
        DataLoaderRegistry().apply {
            loaders.forEach {
                register(it::class.qualifiedName, it)
            }
        }
}