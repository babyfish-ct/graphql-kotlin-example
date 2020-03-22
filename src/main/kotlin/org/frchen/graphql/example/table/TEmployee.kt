package org.frchen.graphql.example.table

import org.frchen.graphql.example.model.Gender
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object TEmployee: IdTable<Long>("employee") {

    override val id = long("id")
        .autoIncrement(idSeqName = "employee_id_seq")
        .entityId()
        .primaryKey()

    val name = varchar("name", 50)

    val gender = enumerationByName("gender", 6, Gender::class)

    var mobile = varchar("mobile", 20)

    val departmentId = long("department_id")
        .references(TDepartment.id, onDelete = ReferenceOption.CASCADE)

    val supervisorId = long("supervisor_id")
        .references(TEmployee.id, onDelete = ReferenceOption.SET_NULL)
        .nullable()
}