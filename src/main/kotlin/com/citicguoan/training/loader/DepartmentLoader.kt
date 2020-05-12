package com.citicguoan.training.loader

import com.citicguoan.training.dal.DepartmentRepository
import com.citicguoan.training.loader.common.AbstractValueLoader
import com.citicguoan.training.loader.common.DataLoaderComponent
import com.citicguoan.training.model.Department
import org.springframework.transaction.PlatformTransactionManager

@DataLoaderComponent
internal open class DepartmentLoader(
    transactionManager: PlatformTransactionManager,
    departmentRepository: DepartmentRepository
) : AbstractValueLoader<Long, Department>(
    transactionManager,
    departmentRepository::findByIds,
    Department::id,
    { setMaxBatchSize(256) }
)