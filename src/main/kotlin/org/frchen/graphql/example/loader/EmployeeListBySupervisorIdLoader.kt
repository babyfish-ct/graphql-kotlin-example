package org.frchen.graphql.example.loader

import org.frchen.graphql.example.model.Employee
import org.frchen.graphql.example.repository.EmployeeRepository
import org.springframework.transaction.PlatformTransactionManager

@DataLoaderComponent
open class EmployeeListBySupervisorIdLoader(
    transactionManager: PlatformTransactionManager,
    employeeRepository: EmployeeRepository
) : AbstractListLoader<Long, Employee>(
    transactionManager,
    employeeRepository::findBySupervisorIds,
    { it._supervisorId ?: error("bug") },
    { setMaxBatchSize(16) }
)