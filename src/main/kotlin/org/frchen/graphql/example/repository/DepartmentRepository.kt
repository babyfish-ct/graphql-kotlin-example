package org.frchen.graphql.example.repository

import org.frchen.graphql.example.model.Department
import org.frchen.graphql.example.model.Location
import org.frchen.graphql.example.table.TDepartment
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
open class DepartmentRepository : AbstractRepository<Department>(
    Binder(TDepartment.id, Department::id),
    Binder(TDepartment.name, Department::name),
    Binder(TDepartment.location, Department::location)
) {
    companion object {
        private val T = TDepartment
    }

    fun find(
        name: String?,
        location: Location?,
        propNames: Collection<String>
    ): List<Department> =
        T
            .slice(columns(propNames))
            .selectAll()
            .apply {
                name
                    ?.takeIf { it.isNotEmpty() }
                    ?.let {
                        andWhere { T.name smartLike it }
                    }
                location
                    ?.let {
                        andWhere { T.location eq it }
                    }
            }
            .map {
                toEntity(it, propNames)
            }

    fun findByIds(
        ids: Collection<Long>,
        propNames: Collection<String>
    ) : List<Department> =
        T
            .slice(columns(propNames))
            .select { T.id inList ids }
            .map {
                toEntity(it, propNames)
            }

    override fun newEntity()= Department()
}