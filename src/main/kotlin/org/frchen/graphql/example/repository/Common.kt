package org.frchen.graphql.example.repository

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.sql.*
import java.lang.IllegalArgumentException
import kotlin.reflect.KCallable
import kotlin.reflect.KMutableProperty1

infix fun Column<String>.smartLike(pattern: String) =
    this.let { column ->
        SqlExpressionBuilder.run { ->
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
                    column.lowerCase() like it
                }
        }
    }

data class Binder<E> private constructor(
    val column: Column<*>,
    val prop: KMutableProperty1<E, *>,
    val name: String,
    val alwaysSelect: Boolean = false
) {
    constructor(
        column: Column<*>,
        prop: KMutableProperty1<E, *>
    ) : this(
        column,
        prop,
        prop.name,
        column == (column.table as IdTable<*>).id
    )

    constructor(
        column: Column<*>,
        prop: KMutableProperty1<E, *>,
        associationMethod: KCallable<*>
    ) : this(column, prop, associationMethod.name, true)

    @Suppress("UNCHECKED_CAST")
    fun bind(entity:E, row: ResultRow) {
        (prop as  KMutableProperty1<E, Any?>)
            .set(
                entity,
                row[column]
                    .let {
                        if (it is EntityID<*>) {
                            it.value
                        } else {
                            it
                        }
                    }
            )
    }
}

abstract class AbstractRepository<E>(
    vararg binders: Binder<E>
) {
    // 此属性必须为open, 否则AOP代理之后为null
    protected open val binderMap: Map<String, Binder<E>> =
        binders.associateBy { it.name }

    protected abstract fun newEntity(): E

    protected fun columns(propNames: Collection<String>): List<Column<*>> =
        propNames
            .mapNotNull { binderMap[it]?.column }
            .let { columns ->
                binderMap
                    .values
                    .filter { it.alwaysSelect }
                    .map { it.column }
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        HashSet<Column<*>>().apply {
                            this += columns
                            this += it
                        }.toList()
                    }
                    ?: columns
            }

    protected fun toEntity(row: ResultRow, propNames: Collection<String>): E =
        newEntity().apply {
            binderMap
                .values
                .filter { it.alwaysSelect || propNames.contains(it.name) }
                .map {
                    it.bind(this, row)
                }
        }
}
