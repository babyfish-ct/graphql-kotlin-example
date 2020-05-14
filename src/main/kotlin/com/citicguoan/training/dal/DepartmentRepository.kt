package com.citicguoan.training.dal

import com.citicguoan.training.dal.common.orderBy
import com.citicguoan.training.dal.common.smartLike
import com.citicguoan.training.dal.common.tryLimit
import com.citicguoan.training.model.Department
import com.citicguoan.training.model.sort.DepartmentSortedType
import com.citicguoan.training.table.TDepartment
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository

interface DepartmentRepository {

    fun findByIds(ids: Collection<Long>): List<Department>

    fun count(name: String?): Int

    fun find(
        name: String?,
        sortedType: DepartmentSortedType,
        descending: Boolean,
        limit: Int?,
        offset: Int?
    ): List<Department>

    fun insert(name: String): Long
}

@Repository
internal open class DepartmentRepositoryImpl : DepartmentRepository {

    companion object {

        private val T = TDepartment

        private val MAPPER: (ResultRow) -> Department = {
            Department(
                id = it[T.id].value,
                name = it[T.name]
            )
        }
    }

    override fun findByIds(ids: Collection<Long>): List<Department> =
        T
            .slice(T.columns)
            .select {  T.id inList ids }
            .map(MAPPER)

    override fun count(name: String?): Int =
        T.id.count().let { countExpr ->
            T
                .slice(countExpr)
                .selectAll()
                .applyConditions(name)
                .map { it[countExpr] }
                .first()
        }

    override fun find(
        name: String?,
        sortedType: DepartmentSortedType,
        descending: Boolean,
        limit: Int?,
        offset: Int?
    ): List<Department> =
        T
            .slice(T.columns)
            .selectAll()
            .applyConditions(name)
            .orderBy(
                descending,
                when(sortedType) {
                    DepartmentSortedType.ID -> T.id
                    DepartmentSortedType.NAME -> T.name
                }
            )
            .tryLimit(limit, offset)
            .map(MAPPER)

    override fun insert(name: String): Long =
        T.insertAndGetId {
            it[T.name] = name
        }.value

    private fun Query.applyConditions(name: String?): Query =
        apply {
            name?.takeIf { it.isNotEmpty() }?.let {
                andWhere { T.name smartLike it }
            }
        }
}
