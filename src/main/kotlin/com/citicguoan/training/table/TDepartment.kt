package com.citicguoan.training.table

import org.jetbrains.exposed.dao.IdTable

object TDepartment : IdTable<Long>("department") {
    override val id = long("department_id")
        .autoIncrement(idSeqName = "department_id_seq")
        .primaryKey()
        .entityId()

    val name = varchar("name", 20)
}