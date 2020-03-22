package org.frchen.graphql.example.loader

import org.frchen.graphql.example.model.Employee
import org.frchen.graphql.example.repository.EmployeeRepository
import org.springframework.transaction.PlatformTransactionManager

@DataLoaderComponent
open class EmployeeListByDepartmentIdLoader(
    transactionManager: PlatformTransactionManager,
    employeeRepository: EmployeeRepository
) : AbstractListLoader<Long, Employee>(
    transactionManager,
    employeeRepository::findByDepartmentIds,
    { it._departmentId },
    { setMaxBatchSize(16) }
)