package org.frchen.graphql.example.loader

import org.frchen.graphql.example.model.Department
import org.frchen.graphql.example.repository.DepartmentRepository
import org.springframework.transaction.PlatformTransactionManager

@DataLoaderComponent
open class DepartmentLoader(
    transactionManager: PlatformTransactionManager,
    departmentRepository: DepartmentRepository
) : AbstractReferenceLoader<Long, Department>(
    transactionManager,
    departmentRepository::findByIds,
    keyGetter = {it.id},
    optionsInitializer = {setMaxBatchSize(256)}
)