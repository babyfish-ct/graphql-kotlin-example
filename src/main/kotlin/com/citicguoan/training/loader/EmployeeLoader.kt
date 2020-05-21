package com.citicguoan.training.loader

import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.loader.common.AbstractValueLoader
import com.citicguoan.training.loader.common.DataLoaderComponent
import com.citicguoan.training.model.Employee
import org.springframework.transaction.PlatformTransactionManager

@DataLoaderComponent
internal open class EmployeeLoader(
    transactionManager: PlatformTransactionManager,
    employeeRepository: EmployeeRepository
) : AbstractValueLoader<Long, Employee>(
    transactionManager,
    employeeRepository::findByIds,
    Employee::id,
    { setMaxBatchSize(256) }
)