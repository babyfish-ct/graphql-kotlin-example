package org.frchen.graphql.example.model

import graphql.schema.DataFetchingEnvironment
import org.frchen.graphql.example.loader.EmployeeListByDepartmentIdLoader
import org.frchen.graphql.example.loader.loadListAsync
import java.util.concurrent.CompletableFuture

data class Department(
    var id: Long = 0L,
    var name: String = "",
    var location: Location = Location.BEIJING,
    var employees: List<Employee> = emptyList()
) {

    fun employees(env: DataFetchingEnvironment): CompletableFuture<List<Employee>> =
        env.loadListAsync<Long, Employee, EmployeeListByDepartmentIdLoader>(id)
}