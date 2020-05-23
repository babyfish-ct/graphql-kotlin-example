package com.citicguoan.training.bll

import com.citicguoan.training.bll.exception.BusinessException
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
    open fun deleteDepartment(id: Long): Boolean =
        employeeRepository
            .findByDepartmentIds(listOf(id))
            .takeIf { it.isNotEmpty() }
            ?.let { list ->
                throw BusinessException(
                    "CANNOT_DELETE_DEPARTMENT_WITH_EMPLOYEES",
                    "Cannot delete the department $id because it has employees",
                    mapOf(
                        "departmentId" to id,
                        "employees" to list.map { it ->
                            mapOf(
                                "id" to it.id,
                                "name" to it.name
                            )
                        }
                    )
                )
            } ?: departmentRepository.delete(id) != 0

    @Transactional
    open fun createEmployee(input: EmployeeInput): Long =
        employeeRepository.insert(input)

    @Transactional
    open fun modifyEmployee(id: Long, input: EmployeeInput): Boolean {
        validateSupervisorReferenceCycle(id, input.supervisorId)
        return employeeRepository.update(id, input) != 0
    }

    @Transactional
    open fun deleteEmployee(id: Long): Boolean =
        employeeRepository
            .findBySupervisorIds(listOf(id))
            .takeIf{ it.isNotEmpty() }
            ?.let { list ->
                throw BusinessException(
                    "CANNOT_DELETE_DEPARTMENT_WITH_SUBORDINATES",
                    "Cannot delete the employee $id because it has subordinates",
                    mapOf(
                        "employeeId" to id,
                        "subordinates" to list.map { emp ->
                            mapOf(
                                "id" to emp.id,
                                "name" to emp.name
                            )
                        }
                    )
                )
            } ?:employeeRepository.delete(id) != 0

    private fun validateSupervisorReferenceCycle(id: Long, supervisorId: Long?) {
        if (supervisorId === null) {
            return
        }
        if (id == supervisorId) {
            throw IllegalArgumentException("New employee has supervisor cycle")
        }
        employeeRepository
            .findByIds(listOf(supervisorId))
            .firstOrNull()
            ?.let {
                validateSupervisorReferenceCycle(id, it.supervisorId)
            }
    }
}