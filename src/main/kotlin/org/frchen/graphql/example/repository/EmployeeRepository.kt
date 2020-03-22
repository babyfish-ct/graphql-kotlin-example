package org.frchen.graphql.example.repository

import org.frchen.graphql.example.model.Employee
import org.frchen.graphql.example.model.Gender
import org.frchen.graphql.example.table.TEmployee
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
open class EmployeeRepository : AbstractRepository<Employee>(
    Binder(TEmployee.id, Employee::id),
    Binder(TEmployee.name, Employee::name),
    Binder(TEmployee.gender, Employee::gender),
    Binder(TEmployee.mobile, Employee::mobile),
    Binder(TEmployee.departmentId, Employee::_departmentId, Employee::department),
    Binder(TEmployee.supervisorId, Employee::_supervisorId, Employee::supervisor)
) {
    companion object {
        private val T = TEmployee
    }

    open fun find(
        name: String?,
        gender: Gender?,
        propNames: Collection<String>
    ): List<Employee> =
        T
            .slice(columns(propNames))
            .selectAll()
            .apply {
                name
                    ?.takeIf { it.isNotEmpty() }
                    ?.let {
                        andWhere { T.name smartLike it }
                    }
                gender
                    ?.let {
                        andWhere { T.gender eq it }
                    }
            }
            .map {
                toEntity(it, propNames)
            }

    open fun findByIds(
        ids: Collection<Long>,
        propNames: Collection<String>
    ): List<Employee> =
        T
            .slice(columns(propNames))
            .select { T.id inList ids }
            .map {
                toEntity(it, propNames)
            }

    open fun findByDepartmentIds(
        departmentIds: Collection<Long>,
        propNames: Collection<String>
    ): List<Employee> =
        T
            .slice(columns(propNames))
            .select { T.departmentId inList departmentIds }
            .map {
                toEntity(it, propNames)
            }

    open fun findBySupervisorIds(
        supervisorIds: Collection<Long>,
        propNames: Collection<String>
    ): List<Employee> =
        T
            .slice(columns(propNames))
            .select { T.supervisorId inList supervisorIds }
            .map {
                toEntity(it, propNames)
            }

    override fun newEntity() = Employee()
}