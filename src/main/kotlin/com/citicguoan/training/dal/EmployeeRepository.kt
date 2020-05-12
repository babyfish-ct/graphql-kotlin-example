package com.citicguoan.training.dal

import com.citicguoan.training.model.Employee
import com.citicguoan.training.model.Gender
import com.citicguoan.training.model.input.CreateEmployeeInput
import com.citicguoan.training.table.TEmployee
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository
import java.math.BigDecimal

interface EmployeeRepository {

    fun findByIds(ids: Collection<Long>): List<Employee>

    fun findByDepartmentIds(departmentIds: Collection<Long>): List<Employee>

    fun findBySupervisorIds(supervisorIds: Collection<Long>): List<Employee>

    fun find(
        name: String?,
        gender: Gender?,
        minSalary: BigDecimal?,
        maxSalary: BigDecimal?
    ): List<Employee>

    fun findAvgSalaryGroupByDepartments(
        departmentIds: Collection<Long>
    ): List<Pair<Long, BigDecimal?>>

    fun insert(input: CreateEmployeeInput): Long
}

@Repository
internal open class EmployeeRepositoryImpl : EmployeeRepository {

    companion object {

        private val T = TEmployee

        private val MAPPER: (ResultRow) -> Employee = {
            Employee(
                id = it[T.id].value,
                name = it[T.name],
                gender = it[T.gender],
                salary = it[T.salary],
                departmentId = it[T.departmentId],
                supervisorId = it[T.supervisorId]
            )
        }
    }

    override fun findByIds(ids: Collection<Long>): List<Employee> =
        T
            .slice(T.columns)
            .select { T.id inList ids }
            .map(MAPPER)

    override fun findByDepartmentIds(departmentIds: Collection<Long>): List<Employee> =
        T
            .slice(T.columns)
            .select { T.departmentId inList departmentIds }
            .map(MAPPER)

    override fun findBySupervisorIds(supervisorIds: Collection<Long>): List<Employee> =
        T
            .slice(T.columns)
            .select { T.supervisorId inList supervisorIds }
            .map(MAPPER)

    override fun find(
        name: String?,
        gender: Gender?,
        minSalary: BigDecimal?,
        maxSalary: BigDecimal?
    ): List<Employee> =
        T
            .slice(T.columns)
            .selectAll()
            .apply {
                name?.takeIf { it.isNotEmpty() }?.let {
                    andWhere {
                        T.name smartLike it
                    }
                }
                gender?.let {
                    andWhere {
                        T.gender eq gender
                    }
                }
                minSalary?.let {
                    andWhere {
                        T.salary greaterEq it
                    }
                }
                maxSalary?.let {
                    andWhere {
                        T.salary lessEq it
                    }
                }
            }
            .map(MAPPER)

    override fun findAvgSalaryGroupByDepartments(
        departmentIds: Collection<Long>
    ): List<Pair<Long, BigDecimal?>> =
        T.salary.avg().let { avg ->
            T
                .slice(
                    T.departmentId,
                    T.salary.avg()
                )
                .select { T.departmentId inList departmentIds }
                .groupBy(T.departmentId)
                .map {
                    it[T.departmentId] to it[avg]
                }
        }

    override fun insert(input: CreateEmployeeInput): Long =
        T.insertAndGetId {
            it[name] = input.name
            it[gender] = input.gender
            it[salary] = input.salary
            it[departmentId] = input.departmentId
            it[supervisorId] = input.supervisorId
        }.value
}