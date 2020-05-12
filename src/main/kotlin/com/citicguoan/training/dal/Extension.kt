package com.citicguoan.training.dal

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.lowerCase
import java.lang.IllegalArgumentException

infix fun Expression<String>.smartLike(pattern: String): Op<Boolean> =
    let { expr ->
        if (pattern.isEmpty()) {
            throw IllegalArgumentException("pattern cannot be empty")
        }
        pattern
            .toLowerCase()
            .let {
                if (!it.startsWith("%")) {
                    "%$it"
                } else {
                    it
                }
            }
            .let {
                if (!it.endsWith("%")) {
                    "$it%"
                } else {
                    it
                }
            }
            .let {
                SqlExpressionBuilder.run {
                    expr.lowerCase() like it
                }
            }
    }