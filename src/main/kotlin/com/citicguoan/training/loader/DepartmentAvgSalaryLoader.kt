package com.citicguoan.training.loader

import com.citicguoan.training.dal.EmployeeRepository
import com.citicguoan.training.loader.common.AbstractValueLoader
import com.citicguoan.training.loader.common.DataLoaderComponent
import org.springframework.transaction.PlatformTransactionManager
import java.math.BigDecimal

@DataLoaderComponent
internal open class DepartmentAvgSalaryLoader(
    transactionManager: PlatformTransactionManager,
    employeeRepository: EmployeeRepository
) : AbstractValueLoader<Long, Pair<Long, BigDecimal?>>(
    transactionManager,
    employeeRepository::findAvgSalaryGroupByDepartments,
    Pair<Long, *>::first,
    { setMaxBatchSize(256) }
)