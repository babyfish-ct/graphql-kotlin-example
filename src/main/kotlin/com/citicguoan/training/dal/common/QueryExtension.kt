package com.citicguoan.training.dal.common

import com.citicguoan.training.model.common.Limitation
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

fun Query.limit(limitation: Limitation?): Query =
    if (limitation === null) {
        this
    } else {
        limit(limitation.value, limitation.offset)
    }