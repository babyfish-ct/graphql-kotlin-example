package com.citicguoan.training.loader.common

import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

inline fun <
        K,
        R,
        reified L: AbstractValueLoader<K, R>
> DataFetchingEnvironment.loadOptionalReferenceAsync(
    key: K?
): CompletableFuture<R?> =
    if (key === null) {
        CompletableFuture.supplyAsync { null }
    } else {
        this
            .getDataLoader<K, R?>(L::class.qualifiedName)
            .also {
                if (it === null) {
                    throw IllegalStateException("No loader ${L::class.qualifiedName}")
                }
            }
            .load(key)
    }

inline fun <
        K,
        R,
        reified L: AbstractValueLoader<K, R>
> DataFetchingEnvironment.loadRequiredReferenceAsync(
    key: K
): CompletableFuture<R> =
    this
        .getDataLoader<K, R?>(L::class.qualifiedName)
        .also {
            if (it === null) {
                throw IllegalStateException("No loader ${L::class.qualifiedName}")
            }
        }
        .load(key)
        .thenApply {
            it ?: error("No value for required reference, key: $key, Loader: ${L::class.qualifiedName}")
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