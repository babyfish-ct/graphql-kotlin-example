package com.citicguoan.training.model.criteria

import com.citicguoan.training.model.Gender
import java.math.BigDecimal

// This class would better end with 'Input'
// otherwise, graphql-kotlin will add the 'Input' suffix to the GraphQL input type name automatically
// Keep kotlin type name and GraphQL type name is better programming style
data class EmployeeCriteriaInput(
    val name: String?,
    val gender: Gender?,
    val minSalary: BigDecimal?,
    val maxSalary: BigDecimal?,
    val departmentIds: List<Long>?
)