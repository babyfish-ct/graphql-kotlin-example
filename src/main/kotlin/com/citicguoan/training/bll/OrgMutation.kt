package com.citicguoan.training.bll

import com.citicguoan.training.dal.DepartmentRepository
import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.model.input.EmployeeInput
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class OrgMutation(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
) : Mutation  {

    @Transactional
    open fun createDepartment(name: String): Long =
        departmentRepository.insert(name)

    @Transactional
    open fun modifyDepartment(id: Long, name: String): Boolean =
        departmentRepository.update(id, name) != 0

    @Transactional
    open fun deleteDepartment(id: Long): Boolean {
        if (employeeRepository.findByDepartmentIds(listOf(id), 1).isNotEmpty()) {
            throw IllegalArgumentException(
                "Department whose id is $id has employee so that it cannot be deleted"
            )
        }
        return departmentRepository.delete(id) != 0
    }

    @Transactional
    open fun createEmployee(input: EmployeeInput): Long =
        employeeRepository.insert(input)

    @Transactional
    open fun modifyEmployee(id: Long, input: EmployeeInput): Boolean {
        validateSupervisorReferenceCycle(id, input.supervisorId)
        return employeeRepository.update(id, input) != 0
    }

    @Transactional
    open fun deleteEmployee(id: Long): Boolean {
        if (employeeRepository.findBySupervisorIds(listOf(id), 1).isNotEmpty()) {
            throw IllegalArgumentException(
                "Employee whose id is $id has subordinates so that it cannot be deleted"
            )
        }
        return employeeRepository.delete(id) != 0
    }

    private fun validateSupervisorReferenceCycle(id: Long, supervisorId: Long?) {
        if (supervisorId === null) {
            return
        }
        if (id == supervisorId) {
            throw IllegalArgumentException("Supervisor cycle")
        }
        employeeRepository
            .findByIds(listOf(supervisorId))
            .firstOrNull()
            ?.let {
                validateSupervisorReferenceCycle(id, it.supervisorId)
            }
    }
}