package com.citicguoan.training.loader.common

import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.util.concurrent.CompletableFuture

abstract class AbstractDataLoader<K, V> (

    // Not null:
    //     Load data from database repositories without transaction scope(like this demo)
    // Null:
    //     Load data from other sources, such as cache, other micro-services
    transactionManager: PlatformTransactionManager?,

    rawBatchGetter: (Collection<K>) -> List<V>,
    optionsInitializer: (DataLoaderOptions.() -> Unit)? = null
) : DataLoader<K, V>(
    { keys ->
        CompletableFuture.supplyAsync {
            if (transactionManager !== null) {
                transactionManager
                    .getTransaction(
                        DefaultTransactionDefinition().apply {
                            isReadOnly = true
                        }
                    ).let {
                        val result = try {
                            rawBatchGetter(keys)
                        } catch (ex: Throwable) {
                            transactionManager.rollback(it)
                            throw ex
                        }
                        transactionManager.commit(it)
                        result
                    }
            } else
                rawBatchGetter(keys)
        }
    },
    optionsInitializer?.let {
        DataLoaderOptions().apply {
            this.it()
        }
    }
)