package com.citicguoan.training.dal

import com.citicguoan.training.bll.OrgMutation
import com.citicguoan.training.model.Gender
import com.citicguoan.training.model.input.CreateDepartmentInput
import com.citicguoan.training.model.input.CreateEmployeeInput
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
internal open class DatabaseInstaller(
    private val orgMutation: OrgMutation
): ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val developId = orgMutation.createDepartment(
            CreateDepartmentInput("Develop")
        )
        val testId = orgMutation.createDepartment(
            CreateDepartmentInput("Test")
        )

        val jimId = orgMutation.createEmployee(
            CreateEmployeeInput(
                name = "Jim",
                gender = Gender.MALE,
                salary = 10000.toBigDecimal(),
                departmentId = developId,
                supervisorId = null
            )
        )
        orgMutation.createEmployee(
            CreateEmployeeInput(
                name = "Kate",
                gender = Gender.FEMALE,
                salary = 8000.toBigDecimal(),
                departmentId = developId,
                supervisorId = jimId
            )
        )
        orgMutation.createEmployee(
            CreateEmployeeInput(
                name = "Bob",
                gender = Gender.MALE,
                salary = 7000.toBigDecimal(),
                departmentId = developId,
                supervisorId = jimId
            )
        )

        val lindaId = orgMutation.createEmployee(
            CreateEmployeeInput(
                name = "Lina",
                gender = Gender.FEMALE,
                salary = 11000.toBigDecimal(),
                departmentId = testId,
                supervisorId = null
            )
        )
        orgMutation.createEmployee(
            CreateEmployeeInput(
                name = "Smith",
                gender = Gender.MALE,
                salary = 6000.toBigDecimal(),
                departmentId = testId,
                supervisorId = lindaId
            )
        )
        orgMutation.createEmployee(
            CreateEmployeeInput(
                name = "Daria",
                gender = Gender.MALE,
                salary = 5000.toBigDecimal(),
                departmentId = testId,
                supervisorId = lindaId
            )
        )
    }
}