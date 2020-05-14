package com.citicguoan.training.bll

import com.citicguoan.training.dal.DepartmentRepository
import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.model.Department
import com.citicguoan.training.model.Employee
import com.citicguoan.training.model.Gender
import com.citicguoan.training.model.criteria.EmployeeCriteria
import com.citicguoan.training.model.sort.DepartmentSortedType
import com.citicguoan.training.model.sort.EmployeeSortedType
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
open class OrgQuery(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
): Query {

    @Transactional(readOnly = true)
    open fun departmentCount(name: String?): Int =
        departmentRepository.count(name)

    @Transactional(readOnly = true)
    open fun departments(
        name: String?,
        sortedType: DepartmentSortedType?,
        descending: Boolean?,
        limit: Int?,
        offset: Int?
    ): List<Department> =
        departmentRepository.find(
            name,
            sortedType = sortedType ?: DepartmentSortedType.ID,
            descending = descending ?: false,
            limit = limit,
            offset = offset
        )

    @Transactional(readOnly = true)
    open fun employeeCount(criteria: EmployeeCriteria?): Int =
        employeeRepository.count(criteria)

    @Transactional(readOnly = true)
    open fun employees(
        criteria: EmployeeCriteria?,
        sortedType: EmployeeSortedType?,
        descending: Boolean?,
        limit: Int?,
        offset: Int?
    ): List<Employee> =
        employeeRepository.find(
            criteria,
            sortedType = sortedType ?: EmployeeSortedType.ID,
            descending = descending ?: false,
            limit = limit,
            offset = offset
        )
}