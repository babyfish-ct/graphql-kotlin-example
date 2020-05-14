package com.citicguoan.training.model.criteria

import com.citicguoan.training.model.Gender
import java.math.BigDecimal

data class EmployeeCriteria(
    val name: String?,
    val gender: Gender?,
    val minSalary: BigDecimal?,
    val maxSalary: BigDecimal?,
    val departmentIds: List<Long>?
)