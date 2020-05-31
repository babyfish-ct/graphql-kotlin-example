package com.citicguoan.training.table

import org.jetbrains.exposed.sql.Table

object TUser: Table("user") {

    val loginName = varchar("login_name", 20).primaryKey()

    val nickName = varchar("nick_name", 50)

    val password = binary("password", 256)
}