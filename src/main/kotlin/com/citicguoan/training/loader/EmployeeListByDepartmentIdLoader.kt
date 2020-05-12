package com.citicguoan.training.loader

import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.loader.common.AbstractListLoader
import com.citicguoan.training.loader.common.DataLoaderComponent
import com.citicguoan.training.model.Employee
import org.springframework.transaction.PlatformTransactionManager

@DataLoaderComponent
internal open class EmployeeListByDepartmentIdLoader(
    transactionManager: PlatformTransactionManager,
    employeeRepository: EmployeeRepository
) : AbstractListLoader<Long, Employee>(
    transactionManager,
    employeeRepository::findByDepartmentIds,
    Employee::departmentId,
    { setMaxBatchSize(16) }
)