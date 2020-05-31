package com.citicguoan.training.model.result

import com.citicguoan.training.model.User

data class LoginResult(
    val user: User,
    val token: String
)