package org.frchen.graphql.example.service

import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import org.frchen.graphql.example.model.Gender
import org.frchen.graphql.example.model.Location
import org.frchen.graphql.example.repository.DepartmentRepository
import org.frchen.graphql.example.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class Query(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
) : GraphQLQueryResolver {

    @Transactional(readOnly = true)
    open fun findDepartments(name: String?, location: Location?, env: DataFetchingEnvironment) =
        departmentRepository.find(name, location, env.selectionSet.get().keys)

    @Transactional(readOnly = true)
    open fun findEmployees(name: String?, gender: Gender, env: DataFetchingEnvironment) =
        employeeRepository.find(name, gender, env.selectionSet.get().keys)
}