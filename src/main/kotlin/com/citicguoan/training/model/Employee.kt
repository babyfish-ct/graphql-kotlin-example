package com.citicguoan.training.model

import com.citicguoan.training.loader.DepartmentLoader
import com.citicguoan.training.loader.EmployeeLoader
import com.citicguoan.training.loader.EmployeeListBySupervisorIdLoader
import com.citicguoan.training.loader.common.loadListAsync
import com.citicguoan.training.loader.common.loadOptionalValueAsync
import com.citicguoan.training.loader.common.loadRequiredValueAsync
import com.expediagroup.graphql.annotations.GraphQLIgnore
import graphql.schema.DataFetchingEnvironment
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

data class Employee(
    val id: Long,
    val name: String,
    val gender: Gender,
    val salary: BigDecimal,
    @GraphQLIgnore val departmentId: Long,
    @GraphQLIgnore val supervisorId: Long?
) {

    fun department(env: DataFetchingEnvironment): CompletableFuture<Department> =
        env.loadRequiredValueAsync<Long, Department, DepartmentLoader>(departmentId)

    fun supervisor(env: DataFetchingEnvironment): CompletableFuture<Employee?> =
        env.loadOptionalValueAsync<Long, Employee, EmployeeLoader>(supervisorId)

    fun subordinates(env: DataFetchingEnvironment): CompletableFuture<List<Employee>> =
        env.loadListAsync<Long, Employee, EmployeeListBySupervisorIdLoader>(id)
}