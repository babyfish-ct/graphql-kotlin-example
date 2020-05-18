package com.citicguoan.training.model

import com.citicguoan.training.model.common.Page

class EmployeePage(
    page: Page,
    val entities: List<Employee>
): Page(page)