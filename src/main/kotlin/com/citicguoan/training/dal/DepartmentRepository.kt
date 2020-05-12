package com.citicguoan.training.dal

import com.citicguoan.training.model.Department
import com.citicguoan.training.model.input.CreateDepartmentInput
import com.citicguoan.training.table.TDepartment
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository

interface DepartmentRepository {

    fun findByIds(ids: Collection<Long>): List<Department>

    fun find(name: String?): List<Department>

    fun insert(input: CreateDepartmentInput): Long
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

    override fun find(name: String?): List<Department> =
        T
            .slice(T.columns)
            .selectAll()
            .apply {
                name?.takeIf { it.isNotEmpty() }?.let {
                    andWhere { T.name smartLike it }
                }
            }
            .map(MAPPER)

    override fun insert(input: CreateDepartmentInput): Long =
        T.insertAndGetId {
            it[name] = input.name
        }.value
}