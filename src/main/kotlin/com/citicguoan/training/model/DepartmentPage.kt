package com.citicguoan.training.model

import com.citicguoan.training.model.common.Page

class DepartmentPage(
    page: Page,
    val entities: List<Department>
) : Page(page)