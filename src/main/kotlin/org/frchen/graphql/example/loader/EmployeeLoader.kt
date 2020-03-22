package org.frchen.graphql.example.loader

import org.frchen.graphql.example.model.Employee
import org.frchen.graphql.example.repository.EmployeeRepository
import org.springframework.transaction.PlatformTransactionManager

@DataLoaderComponent
open class EmployeeLoader(
    transactionManager: PlatformTransactionManager,
    employeeRepository: EmployeeRepository
) : AbstractReferenceLoader<Long, Employee>(
    transactionManager,
    employeeRepository::findByIds,
    { it.id },
    { setMaxBatchSize(256) }
)