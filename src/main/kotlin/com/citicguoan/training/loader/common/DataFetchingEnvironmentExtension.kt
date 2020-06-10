package com.citicguoan.training.loader.common

import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KProperty1

inline fun <
        K,
        V,
        reified L: AbstractValueLoader<K, V>
> DataFetchingEnvironment.loadOptionalValueAsync(
    key: K?,
    keyProp: KProperty1<V, K>? = null,
    noinline fakeValueCreator: ((K) -> V)? = null
): CompletableFuture<V?> {
    if ((keyProp === null) != (fakeValueCreator === null)) {
        throw IllegalArgumentException("keyProp and fakeValueCreator must be both null or both non-null")
    }
    if (key === null) {
        return CompletableFuture.completedFuture(null)
    }
    if (keyProp !== null && fakeValueCreator !== null) {
        val selections = mergedField.singleField.selectionSet.selections
        if (selections.size == 1) {
            val selection = selections[0]
            if (selection is Field && selection.name == keyProp.name) {
                return CompletableFuture.completedFuture(fakeValueCreator(key))
            }
        }
    }
    return this
        .getDataLoader<K, V?>(L::class.qualifiedName)
        .also {
            if (it === null) {
                throw IllegalStateException("No loader ${L::class.qualifiedName}")
            }
        }
        .load(key)
}

inline fun <
        K,
        V,
        reified L: AbstractValueLoader<K, V>
> DataFetchingEnvironment.loadRequiredValueAsync(
    key: K,
    keyProp: KProperty1<V, K>? = null,
    noinline fakeValueCreator: ((K) -> V)? = null
): CompletableFuture<V> {
    if ((keyProp === null) != (fakeValueCreator === null)) {
        throw IllegalArgumentException("keyProp and fakeValueCreator must be both null or both non-null")
    }
    if (keyProp !== null && fakeValueCreator !== null) {
        val selections = mergedField.singleField.selectionSet.selections
        if (selections.size == 1) {
            val selection = selections[0]
            if (selection is Field && selection.name == keyProp.name) {
                return CompletableFuture.completedFuture(fakeValueCreator(key))
            }
        }
    }
    return this
        .getDataLoader<K, V?>(L::class.qualifiedName)
        .also {
            if (it === null) {
                throw IllegalStateException("No loader ${L::class.qualifiedName}")
            }
        }
        .load(key)
        .thenApply {
            it ?: error("No value for required reference, key: $key, Loader: ${L::class.qualifiedName}")
        }
}

inline fun <
        K,
        E,
        reified L: AbstractListLoader<K, E>
> DataFetchingEnvironment.loadListAsync(
    key: K
): CompletableFuture<List<E>> =
    if (key === null) {
        CompletableFuture.supplyAsync { emptyList<E>() }
    } else {
        this
            .getDataLoader<K, List<E>>(L::class.qualifiedName)
            .also {
                if (it === null) {
                    throw IllegalStateException("No loader ${L::class.qualifiedName}")
                }
            }
            .load(key)
    }