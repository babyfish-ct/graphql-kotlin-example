package com.citicguoan.training.loader.common

import com.expediagroup.graphql.spring.execution.DataLoaderRegistryFactory
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode

@Configuration
@ConditionalOnMissingBean(DataLoaderRegistryFactory::class)
internal abstract class DataLoaderAutoConfiguration {

    @Bean
    open fun dataLoaderRegistryFactory(): DataLoaderRegistryFactory =
        object: DataLoaderRegistryFactory {
            override fun generate(): DataLoaderRegistry =
                dataLoaderRegistry()
        }

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

    @Lookup
    protected abstract fun dataLoaderRegistry(): DataLoaderRegistry
}