package com.citicguoan.training.loader.common

import org.dataloader.DataLoaderOptions
import org.springframework.transaction.PlatformTransactionManager

abstract class AbstractValueLoader<K, R>(
    transactionManager: PlatformTransactionManager?,
    batchGetter: (Collection<K>) -> Collection<R>,
    keyGetter: (R) -> K,
    optionsInitializer: (DataLoaderOptions.() -> Unit)? = null
) : AbstractDataLoader<K, R?>(
    transactionManager,
    { keys ->
        batchGetter(keys)
            .associateBy(keyGetter)
            .let { map ->
                keys.map { map[it] }
            }
    },
    optionsInitializer
)