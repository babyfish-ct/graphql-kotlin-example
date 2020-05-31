package com.citicguoan.training.dal

import com.citicguoan.training.bll.OrgMutation
import com.citicguoan.training.bll.context.AppContext
import com.citicguoan.training.model.Gender
import com.citicguoan.training.model.User
import com.citicguoan.training.model.input.EmployeeInput
import com.citicguoan.training.table.TUser
import org.jetbrains.exposed.sql.batchInsert
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.security.MessageDigest

@Component
internal open class DatabaseInstaller(
    private val orgMutation: OrgMutation,
    private val transactionManager: PlatformTransactionManager
): ApplicationRunner {

    override fun run(args: ApplicationArguments?) {

        val user = User(
            "admin",
            nickName = "Crazy frog",
            password = MessageDigest
                .getInstance("SHA")
                .digest("123".toByteArray())
        )
        insertUsers(user)

        val ctx = AppContext(user)

        val developId = orgMutation.createDepartment("Develop", ctx)
        val testId = orgMutation.createDepartment("Test", ctx)

        val jimId = orgMutation.createEmployee(
            EmployeeInput(
                name = "Jim",
                gender = Gender.MALE,
                salary = 10000.toBigDecimal(),
                departmentId = developId,
                supervisorId = null
            ),
            ctx
        )
        orgMutation.createEmployee(
            EmployeeInput(
                name = "Kate",
                gender = Gender.FEMALE,
                salary = 8000.toBigDecimal(),
                departmentId = developId,
                supervisorId = jimId
            ),
            ctx
        )
        orgMutation.createEmployee(
            EmployeeInput(
                name = "Bob",
                gender = Gender.MALE,
                salary = 7000.toBigDecimal(),
                departmentId = developId,
                supervisorId = jimId
            ),
            ctx
        )

        val lindaId = orgMutation.createEmployee(
            EmployeeInput(
                name = "Linda",
                gender = Gender.FEMALE,
                salary = 11000.toBigDecimal(),
                departmentId = testId,
                supervisorId = null
            ),
            ctx
        )
        orgMutation.createEmployee(
            EmployeeInput(
                name = "Smith",
                gender = Gender.MALE,
                salary = 6000.toBigDecimal(),
                departmentId = testId,
                supervisorId = lindaId
            ),
            ctx
        )
        orgMutation.createEmployee(
            EmployeeInput(
                name = "Daria",
                gender = Gender.FEMALE,
                salary = 5000.toBigDecimal(),
                departmentId = testId,
                supervisorId = lindaId
            ),
            ctx
        )
    }

    private fun insertUsers(vararg users: User) {
        DefaultTransactionDefinition().apply {
            propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRED
        }.let {
            transactionManager.getTransaction(it)
        }.let {
            try {
                TUser.batchInsert(users.asIterable()) { user ->
                    this[TUser.loginName] = user.loginName
                    this[TUser.nickName] = user.nickName
                    this[TUser.password] = user.password
                }
            } catch (ex: Throwable) {
                transactionManager.rollback(it)
                throw ex
            }
            transactionManager.commit(it)
        }
    }
}