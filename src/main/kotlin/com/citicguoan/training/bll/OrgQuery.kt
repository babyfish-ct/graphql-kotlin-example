package com.citicguoan.training.bll

import com.citicguoan.training.dal.DepartmentRepository
import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.model.Department
import com.citicguoan.training.model.Employee
import com.citicguoan.training.model.Gender
import com.expediagroup.graphql.spring.operations.Query
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
open class OrgQuery(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
): Query {

    @Transactional(readOnly = true)
    open fun departments(
        name: String?
    ): List<Department> =
        departmentRepository.find(name)

    @Transactional(readOnly = true)
    open fun employees(
        name: String?,
        gender: Gender?,
        minSalary: BigDecimal?,
        maxSalary: BigDecimal?
    ): List<Employee> =
        employeeRepository.find(
            name = name,
            gender = gender,
            minSalary = minSalary,
            maxSalary = maxSalary
        )
}