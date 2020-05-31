package com.citicguoan.training.bll.exception

import com.citicguoan.training.model.Employee
import java.lang.RuntimeException

class BusinessException(
    val code: String,
    message: String,
    val fields: Map<String, Any>? = null
) : RuntimeException(message)

fun illegalLoginName(loginName: String): Nothing {
    throw BusinessException(
        "ILLEGAL_LOGIN_NAME",
        "The user whose login name is '$loginName' does not exists"
    )
}

fun illegalPassword(): Nothing {
    throw BusinessException(
        "ILLEGAL_PASSWORD",
        "The password is illegal"
    )
}

fun unauthorized(): Nothing {
    throw BusinessException(
        "UNAUTHORIZED",
        "Unauthorized, please login"
    )
}

fun illegalDepartmentId(departmentId: Long): Nothing {
    throw BusinessException(
        "ILLEGAL_DEPARTMENT_ID",
        "The department whose id is $departmentId does not exists",
        mapOf(
            "departmentId" to departmentId
        )
    )
}

fun illegalSupervisorId(supervisorId: Long): Nothing {
    throw BusinessException(
        "ILLEGAL_SUPERVISOR_ID",
        "The supervisor whose id is $supervisorId does not exists",
        mapOf(
            "supervisorId" to supervisorId
        )
    )
}

fun cannotDeleteDepartmentWithEmployees(
    departmentId: Long,
    employees: Collection<Employee>
): Nothing {
    throw BusinessException(
        "CANNOT_DELETE_DEPARTMENT_WITH_EMPLOYEES",
        "Cannot delete the department whose id is $departmentId because it has employees",
        mapOf(
            "departmentId" to departmentId,
            "employees" to employees.map { it ->
                mapOf(
                    "id" to it.id,
                    "name" to it.name
                )
            }
        )
    )
}

fun cannotDeleteEmployeeWithSubordinates(
    employeeId: Long,
    subordinates: Collection<Employee>
): Nothing {
    throw BusinessException(
        "CANNOT_DELETE_EMPLOYEE_WITH_SUBORDINATES",
        "Cannot delete the employee whose id is $employeeId because it has subordinates",
        mapOf(
            "employeeId" to employeeId,
            "subordinates" to subordinates.map {
                mapOf(
                    "id" to it.id,
                    "name" to it.name
                )
            }
        )
    )
}

fun supervisorCycle(
    employeeId: Long,
    supervisors: Collection<Employee>
): Nothing {
    throw BusinessException(
        "SUPERVISOR_CYCLE",
        "New employee has supervisor cycle",
        mapOf(
            "employeeId" to employeeId,
            "supervisors" to supervisors.map {
                mapOf("id" to it.id, "name" to it.name)
            }
        )
    )
}