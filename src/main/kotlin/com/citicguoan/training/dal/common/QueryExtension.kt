package com.citicguoan.training.dal.common

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import java.lang.IllegalArgumentException

fun Query.orderBy(descending: Boolean, vararg expressions: Expression<*>): Query {
    val sortOrder =
        if (descending) {
            SortOrder.DESC
        } else {
            SortOrder.ASC
        }
    expressions.forEach {
        orderBy(it, sortOrder)
    }
    return this
}

fun Query.tryLimit(limit: Int?, offset: Int?): Query =
    if (limit === null) {
        if (offset !== null) {
            throw IllegalArgumentException("limit cannot be null when offset is not null")
        }
        this
    } else {
        limit(limit, offset ?: 0)
    }