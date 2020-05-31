package com.citicguoan.training.bll

import com.citicguoan.training.bll.context.AppContext
import com.citicguoan.training.bll.exception.*
import com.citicguoan.training.dal.DepartmentRepository
import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.model.Employee
import com.citicguoan.training.model.input.EmployeeInput
import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class OrgMutation(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
) : Mutation  {

    @Transactional
    open fun createDepartment(
        name: String,
        @GraphQLContext ctx: AppContext
    ): Long {
        if (ctx.user === null) {
            unauthorized()
        }
        return departmentRepository.insert(name)
    }

    @Transactional
    open fun modifyDepartment(
        id: Long,
        name: String,
        @GraphQLContext ctx: AppContext
    ): Boolean {
        if (ctx.user === null) {
            unauthorized()
        }
        return departmentRepository.update(id, name) != 0
    }

    @Transactional
    open fun deleteDepartment(
        id: Long,
        @GraphQLContext ctx: AppContext
    ): Boolean {
        if (ctx.user === null) {
            unauthorized()
        }
        return employeeRepository
            .findByDepartmentIds(listOf(id))
            .takeIf { it.isNotEmpty() }
            ?.let {
                cannotDeleteDepartmentWithEmployees(id, it)
            } ?: departmentRepository.delete(id) != 0
    }

    @Transactional
    open fun createEmployee(
        input: EmployeeInput,
        @GraphQLContext ctx: AppContext
    ): Long {
        if (ctx.user === null) {
            unauthorized()
        }
        return employeeRepository.insert(
            input.apply {
                if (departmentRepository.findByIds(listOf(departmentId)).isEmpty()) {
                    illegalDepartmentId(departmentId)
                }
                if (supervisorId !== null &&
                    employeeRepository.findByIds(listOf(supervisorId)).isEmpty()
                ) {
                    illegalSupervisorId(supervisorId)
                }
            }
        )
    }

    @Transactional
    open fun modifyEmployee(
        id: Long,
        input: EmployeeInput,
        @GraphQLContext ctx: AppContext
    ): Boolean {
        if (ctx.user === null) {
            unauthorized()
        }
        validateSupervisorReferenceCycle(id, input.supervisorId, mutableListOf())
        return employeeRepository.update(
            id,
            input.apply {
                if (departmentRepository.findByIds(listOf(departmentId)).isEmpty()) {
                    illegalDepartmentId(departmentId)
                }
            }
        ) != 0
    }

    @Transactional
    open fun deleteEmployee(
        id: Long,
        @GraphQLContext ctx: AppContext
    ): Boolean {
        if (ctx.user === null) {
            unauthorized()
        }
        return employeeRepository
            .findBySupervisorIds(listOf(id))
            .takeIf { it.isNotEmpty() }
            ?.let {
                cannotDeleteEmployeeWithSubordinates(id, it)
            } ?: employeeRepository.delete(id) != 0
    }

    private fun validateSupervisorReferenceCycle(
        id: Long,
        supervisorId: Long?,
        supervisors: MutableList<Employee>
    ) {
        if (supervisorId === null) {
            return
        }
        val supervisor = employeeRepository
            .findByIds(listOf(supervisorId))
            .firstOrNull()
        if (supervisor === null) {
            illegalSupervisorId(supervisorId)
        }
        supervisors.add(supervisor)
        if (id == supervisorId) {
            supervisorCycle(
                id,
                supervisors.let {
                    mutableListOf(it[it.size - 1]).apply {
                        addAll(it)
                    }
                }
            )
        }
        employeeRepository
            .findByIds(listOf(supervisorId))
            .firstOrNull()
            ?.let {
                validateSupervisorReferenceCycle(id, it.supervisorId, supervisors)
            }
    }
}