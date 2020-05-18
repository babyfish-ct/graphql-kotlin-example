package com.citicguoan.training.bll

import com.citicguoan.training.dal.DepartmentRepository
import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.model.*
import com.citicguoan.training.model.common.Page
import com.citicguoan.training.model.criteria.EmployeeCriteria
import com.citicguoan.training.model.sort.DepartmentSortedType
import com.citicguoan.training.model.sort.EmployeeSortedType
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class OrgQuery(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
): Query {

    @Transactional(readOnly = true)
    open fun department(id: Long): Department? =
        departmentRepository
            .findByIds(listOf(id))
            .firstOrNull()

    @Transactional(readOnly = true)
    open fun departmentPage(
        name: String?,
        sortedType: DepartmentSortedType,
        descending: Boolean,
        pageNo: Int,
        pageSize: Int
    ): DepartmentPage =
        Page
            .of(
                pageNo = pageNo,
                pageSize = pageSize,
                rowCount = departmentRepository.count(name)
            )
            .let {
                DepartmentPage(
                    it,
                    departmentRepository.find(
                        name,
                        sortedType = sortedType,
                        descending = descending,
                        limitation = it.limitation
                    )
                )
            }

    @Transactional(readOnly = true)
    open fun employee(id: Long): Employee? =
        employeeRepository
            .findByIds(listOf(id))
            .firstOrNull()

    @Transactional(readOnly = true)
    open fun employeePage(
        criteria: EmployeeCriteria?,
        sortedType: EmployeeSortedType,
        descending: Boolean,
        pageNo: Int,
        pageSize: Int
    ): EmployeePage =
        Page
            .of(
                pageNo = pageNo,
                pageSize = pageSize,
                rowCount = employeeRepository.count(criteria)
            )
            .let {
                EmployeePage(
                    it,
                    employeeRepository.find(
                        criteria,
                        sortedType = sortedType,
                        descending = descending,
                        limitation = it.limitation
                    )
                )
            }
}