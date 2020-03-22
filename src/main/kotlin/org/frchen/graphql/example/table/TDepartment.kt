package org.frchen.graphql.example.table

import org.frchen.graphql.example.model.Location
import org.jetbrains.exposed.dao.IdTable

object TDepartment: IdTable<Long>("department") {

    override val id = long("id")
        .autoIncrement(idSeqName = "department_id_seq")
        .entityId()
        .primaryKey()

    val name = varchar("name", 50)

    val location = enumerationByName("location", 20, Location::class)
}
