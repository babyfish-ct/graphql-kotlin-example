package com.citicguoan.training.model

import com.citicguoan.training.loader.DepartmentAvgSalaryLoader
import com.citicguoan.training.loader.EmployeeListByDepartmentIdLoader
import com.citicguoan.training.loader.common.loadListAsync
import com.citicguoan.training.loader.common.loadOptionalValueAsync
import graphql.schema.DataFetchingEnvironment
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

data class Department(
    val id: Long,
    val name: String
) {
    fun employees(env: DataFetchingEnvironment): CompletableFuture<List<Employee>> =
        env.loadListAsync<Long, Employee, EmployeeListByDepartmentIdLoader>(id)

    fun avgSalary(env: DataFetchingEnvironment): CompletableFuture<BigDecimal?> =
        env
            .loadOptionalValueAsync<
                    Long,
                    Pair<Long, BigDecimal?>,
                    DepartmentAvgSalaryLoader
            >(id)
            .thenApply { it?.second }

    companion object {
        // When no properties except id are required by GraphQL
        fun fakeWithId(id: Long): Department =
            Department(id, "")
    }
}
