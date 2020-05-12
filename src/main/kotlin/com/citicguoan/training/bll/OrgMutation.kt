package com.citicguoan.training.bll

import com.citicguoan.training.dal.DepartmentRepository
import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.model.input.CreateDepartmentInput
import com.citicguoan.training.model.input.CreateEmployeeInput
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class OrgMutation(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
) : Mutation  {

    @Transactional
    open fun createDepartment(input: CreateDepartmentInput): Long =
        departmentRepository.insert(input)

    @Transactional
    open fun createEmployee(input: CreateEmployeeInput): Long =
        employeeRepository.insert(input)
}