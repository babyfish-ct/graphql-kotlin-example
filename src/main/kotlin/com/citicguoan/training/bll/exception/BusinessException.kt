package com.citicguoan.training.bll.exception

import java.lang.RuntimeException

class BusinessException(
    val code: String,
    message: String,
    val fields: Map<String, Any>? = null
) : RuntimeException(message)
