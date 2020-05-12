package com.citicguoan.training.table

import com.citicguoan.training.model.Gender
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object TEmployee: IdTable<Long>("employee") {

    override val id = long("employee_id")
        .autoIncrement(idSeqName = "employee_id_seq")
        .entityId()

    val name = varchar("name", 20)

    val gender = enumerationByName("gender", 6, Gender::class)

    val salary = decimal("salary", 10, 0)

    val departmentId = long("department_id")
        .references(TDepartment.id, onDelete = ReferenceOption.CASCADE)

    val supervisorId = long("supervisor_id")
        .references(TEmployee.id, onDelete = ReferenceOption.SET_NULL)
        .nullable()
}