package com.citicguoan.training.model.input

import com.citicguoan.training.model.Gender
import java.math.BigDecimal

data class CreateEmployeeInput(
    val name: String,
    val gender: Gender,
    val salary: BigDecimal,
    val departmentId: Long,
    val supervisorId: Long?
)