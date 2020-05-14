package com.citicguoan.training.dal

import com.citicguoan.training.dal.common.orderBy
import com.citicguoan.training.dal.common.smartLike
import com.citicguoan.training.dal.common.tryLimit
import com.citicguoan.training.model.Employee
import com.citicguoan.training.model.Gender
import com.citicguoan.training.model.criteria.EmployeeCriteria
import com.citicguoan.training.model.input.EmployeeInput
import com.citicguoan.training.model.sort.EmployeeSortedType
import com.citicguoan.training.table.TDepartment
import com.citicguoan.training.table.TEmployee
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository
import java.lang.IllegalArgumentException
import java.math.BigDecimal

interface EmployeeRepository {

    fun findByIds(ids: Collection<Long>): List<Employee>

    fun findByDepartmentIds(departmentIds: Collection<Long>): List<Employee>

    fun findBySupervisorIds(supervisorIds: Collection<Long>): List<Employee>

    fun count(criteria: EmployeeCriteria?): Int

    fun find(
        criteria: EmployeeCriteria?,
        sortedType: EmployeeSortedType,
        descending: Boolean,
        limit: Int?,
        offset: Int?
    ): List<Employee>

    fun findAvgSalaryGroupByDepartments(
        departmentIds: Collection<Long>
    ): List<Pair<Long, BigDecimal?>>

    fun insert(input: EmployeeInput): Long
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

    override fun count(criteria: EmployeeCriteria?): Int =
        T.id.count().let { countExpr ->
            T
                .slice(countExpr)
                .selectAll()
                .applyConditions(criteria)
                .map { it[countExpr] }
                .first()
        }

    override fun find(
        criteria: EmployeeCriteria?,
        sortedType: EmployeeSortedType,
        descending: Boolean,
        limit: Int?,
        offset: Int?
    ): List<Employee> =
        T
            .let {
                if (sortedType == EmployeeSortedType.DEPARTMENT_NAME) {
                    it.innerJoin(
                        TDepartment,
                        onColumn = { it.departmentId },
                        otherColumn = { it.id }
                    )
                } else {
                    it
                }
            }
            .slice(T.columns)
            .selectAll()
            .applyConditions(criteria)
            .orderBy(
                descending,
                *when (sortedType) {
                    EmployeeSortedType.ID -> arrayOf(T.id)
                    EmployeeSortedType.NAME -> arrayOf(T.name)
                    EmployeeSortedType.SALARY -> arrayOf(T.salary, T.id)
                    EmployeeSortedType.DEPARTMENT_ID ->
                        arrayOf(T.departmentId, T.id)
                    EmployeeSortedType.DEPARTMENT_NAME ->
                        arrayOf(TDepartment.name, T.id)
                }
            )
            .tryLimit(limit, offset)
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

    override fun insert(input: EmployeeInput): Long =
        T.insertAndGetId {
            it[name] = input.name
            it[gender] = input.gender
            it[salary] = input.salary
            it[departmentId] = input.departmentId
            it[supervisorId] = input.supervisorId
        }.value

    private fun Query.applyConditions(criteria: EmployeeCriteria?): Query =
        apply {
            criteria?.name?.takeIf { it.isNotEmpty() }?.let {
                andWhere {
                    T.name smartLike it
                }
            }
            criteria?.gender?.let {
                andWhere {
                    T.gender eq it
                }
            }
            criteria?.minSalary?.let {
                andWhere {
                    T.salary greaterEq it
                }
            }
            criteria?.maxSalary?.let {
                andWhere {
                    T.salary lessEq it
                }
            }
        }
}