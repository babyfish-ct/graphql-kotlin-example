package com.citicguoan.training.model.common

import java.lang.IllegalArgumentException

data class Limitation(
    val value: Int,
    val offset: Int = 0
) {
    companion object {
        fun of(limit: Int?, offset: Int? = null): Limitation? =
            if (limit === null) {
                if (offset !== null) {
                    throw IllegalArgumentException("offset must be null when limit is null")
                }
                null
            } else {
                Limitation(limit, offset ?: 0)
            }
    }
}