package com.citicguoan.training.dal

import com.citicguoan.training.bll.OrgMutation
import com.citicguoan.training.model.Gender
import com.citicguoan.training.model.input.EmployeeInput
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
internal open class DatabaseInstaller(
    private val orgMutation: OrgMutation
): ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val developId = orgMutation.createDepartment("Develop")
        val testId = orgMutation.createDepartment("Test")

        val jimId = orgMutation.createEmployee(
            EmployeeInput(
                name = "Jim",
                gender = Gender.MALE,
                salary = 10000.toBigDecimal(),
                departmentId = developId,
                supervisorId = null
            )
        )
        orgMutation.createEmployee(
            EmployeeInput(
                name = "Kate",
                gender = Gender.FEMALE,
                salary = 8000.toBigDecimal(),
                departmentId = developId,
                supervisorId = jimId
            )
        )
        orgMutation.createEmployee(
            EmployeeInput(
                name = "Bob",
                gender = Gender.MALE,
                salary = 7000.toBigDecimal(),
                departmentId = developId,
                supervisorId = jimId
            )
        )

        val lindaId = orgMutation.createEmployee(
            EmployeeInput(
                name = "Linda",
                gender = Gender.FEMALE,
                salary = 11000.toBigDecimal(),
                departmentId = testId,
                supervisorId = null
            )
        )
        orgMutation.createEmployee(
            EmployeeInput(
                name = "Smith",
                gender = Gender.MALE,
                salary = 6000.toBigDecimal(),
                departmentId = testId,
                supervisorId = lindaId
            )
        )
        orgMutation.createEmployee(
            EmployeeInput(
                name = "Daria",
                gender = Gender.FEMALE,
                salary = 5000.toBigDecimal(),
                departmentId = testId,
                supervisorId = lindaId
            )
        )
    }
}