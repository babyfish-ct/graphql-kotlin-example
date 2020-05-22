package com.citicguoan.training.dal

import com.citicguoan.training.dal.common.orderBy
import com.citicguoan.training.dal.common.smartLike
import com.citicguoan.training.dal.common.limit
import com.citicguoan.training.model.Department
import com.citicguoan.training.model.common.Limitation
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
        limitation: Limitation?
    ): List<Department>

    fun insert(name: String): Long

    fun update(id: Long, name: String): Int

    fun delete(id: Long): Int
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
        limitation: Limitation?
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
            .limit(limitation)
            .map(MAPPER)

    override fun insert(name: String): Long =
        T.insertAndGetId {
            it[T.name] = name
        }.value

    override fun update(id: Long, name: String): Int =
        T.update({ T.id eq id }) {
            it[T.name] = name
        }

    override fun delete(id: Long): Int =
        T.deleteWhere { T.id eq id }

    private fun Query.applyConditions(name: String?): Query =
        apply {
            name?.takeIf { it.isNotEmpty() }?.let {
                andWhere { T.name smartLike it }
            }
        }
}
