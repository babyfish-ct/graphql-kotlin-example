package org.frchen.graphql.example.model

import com.fasterxml.jackson.annotation.JsonIgnore
import graphql.schema.DataFetchingEnvironment
import org.frchen.graphql.example.loader.*
import java.util.concurrent.CompletableFuture

internal val INVALID_DEPARTMENT = Department()

data class Employee(
    var id: Long = 0L,
    var name: String = "",
    var gender: Gender = Gender.MALE,
    var mobile: String = "",

    @JsonIgnore var _departmentId: Long = 0L,
    @JsonIgnore var _supervisorId: Long? = null
) {

    fun department(env: DataFetchingEnvironment): CompletableFuture<Department> =
        env.loadRequiredReferenceAsync<Long, Department, DepartmentLoader>(_departmentId)

    fun supervisor(env: DataFetchingEnvironment): CompletableFuture<Employee?> =
        env.loadOptionalReferenceAsync<Long, Employee, EmployeeLoader>(_supervisorId)

    fun subordinates(env: DataFetchingEnvironment): CompletableFuture<List<Employee>> =
        env.loadListAsync<Long, Employee, EmployeeListBySupervisorIdLoader>(_supervisorId)
}