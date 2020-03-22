package org.frchen.graphql.example.loader

import graphql.schema.DataFetchingEnvironment
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.lang.IllegalStateException
import java.util.concurrent.CompletableFuture


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Scope(
    ConfigurableBeanFactory.SCOPE_PROTOTYPE,
    proxyMode = ScopedProxyMode.NO
)
@Component
annotation class DataLoaderComponent

data class LoaderKey<K>(
    val value: K,
    val propNames: Collection<String>
)

abstract class AbstractDataLoader<K, V> (

    // Not null:
    //     Load data from database repositories without transaction scope(like this);
    // Null:
    //     Load data from other sources, such as cache, other micro-services
    transactionManager: PlatformTransactionManager?,

    rawBatchGetter: (keys: Collection<K>, propNames: Collection<String>) -> List<V>,
    optionsInitializer: (DataLoaderOptions.() -> Unit)? = null

) : DataLoader<LoaderKey<K>, V>(
    { loaderKeys ->
        CompletableFuture.supplyAsync {
            if (transactionManager !== null) {
                transactionManager
                    .getTransaction(
                        DefaultTransactionDefinition().apply {
                            isReadOnly = true
                        }
                    ).let {
                        val result = try {
                            rawBatchGetter(
                                loaderKeys.map { key -> key.value },
                                loaderKeys.first().propNames
                            )
                        } catch (ex: Throwable) {
                            transactionManager.rollback(it)
                            throw ex
                        }
                        transactionManager.commit(it)
                        result
                    }
            } else
                rawBatchGetter(
                    loaderKeys.map { key -> key.value },
                    loaderKeys.first().propNames
                )
            }
    },
    optionsInitializer?.let {
        DataLoaderOptions().apply {
            this.it()
        }
    }
)

abstract class AbstractReferenceLoader<K, R>(
    transactionManager: PlatformTransactionManager?,
    batchGetter: (keys: Collection<K>, propNames: Collection<String>) -> Collection<R>,
    keyGetter: (R) -> K,
    optionsInitializer: (DataLoaderOptions.() -> Unit)? = null
) : AbstractDataLoader<K, R?>(
    transactionManager,
    { keys, propNames ->
        batchGetter(keys, propNames)
            .associateBy(keyGetter)
            .let { map ->
                keys.map { map[it] }
            }
    },
    optionsInitializer
)

abstract class AbstractListLoader<K, E>(
    transactionManager: PlatformTransactionManager?,
    batchGetter: (keys: Collection<K>, propNames: Collection<String>) -> Collection<E>,
    keyGetter: (E) -> K,
    optionsInitializer: (DataLoaderOptions.() -> Unit)? = null
): AbstractDataLoader<K, List<E>>(
    transactionManager,
    { keys, propNames ->
        batchGetter(keys, propNames)
            .groupBy(keyGetter)
            .let { map ->
                keys.map { map[it] ?: emptyList() }
            }
    },
    optionsInitializer
)

inline fun <K, R, reified L: AbstractReferenceLoader<K, R>> DataFetchingEnvironment.loadOptionalReferenceAsync(
    key: K?
): CompletableFuture<R?> =
    if (key === null) {
        CompletableFuture.supplyAsync { null }
    } else {
        this
            .getDataLoader<LoaderKey<K>, R?>(L::class.qualifiedName)
            .load(LoaderKey(key, this.selectionSet.get().keys))
    }

inline fun <K, R, reified L: AbstractReferenceLoader<K, R>> DataFetchingEnvironment.loadRequiredReferenceAsync(
    key: K
): CompletableFuture<R> =
    this
        .getDataLoader<LoaderKey<K>, R?>(L::class.qualifiedName)
        .load(LoaderKey(key, this.selectionSet.get().keys))
        .thenApply {
            it ?: error("No value for required reference, key: $key, Loader: ${L::class.qualifiedName}")
        }

inline fun <K, E, reified L: AbstractListLoader<K, E>> DataFetchingEnvironment.loadListAsync(
    key: K?
): CompletableFuture<List<E>> =
    if (key === null) {
        CompletableFuture.supplyAsync { emptyList<E>() }
    } else {
        this
            .getDataLoader<LoaderKey<K>, List<E>>(L::class.qualifiedName)
            .load(LoaderKey(key, this.selectionSet.get().keys))
    }