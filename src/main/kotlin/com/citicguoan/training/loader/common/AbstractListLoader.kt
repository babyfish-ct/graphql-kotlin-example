package com.citicguoan.training.loader.common

import org.dataloader.DataLoaderOptions
import org.springframework.transaction.PlatformTransactionManager

abstract class AbstractListLoader<K, E>(
    transactionManager: PlatformTransactionManager?,
    batchGetter: (Collection<K>) -> Collection<E>,
    keyGetter: (E) -> K?,
    optionsInitializer: (DataLoaderOptions.() -> Unit)? = null
): AbstractDataLoader<K, List<E>>(
    transactionManager,
    { keys ->
        batchGetter(keys)
            .groupBy(keyGetter)
            .let { map ->
                keys.map { map[it] ?: emptyList() }
            }
    },
    optionsInitializer
)