package com.citicguoan.training.dal

import com.citicguoan.training.model.User
import com.citicguoan.training.table.TUser
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository

interface UserRepository {

    fun findByLoginName(loginName: String): User?
}

@Repository
internal open class UserRepositoryImpl: UserRepository {

    companion object {
        private val T = TUser
        private val MAPPER: (ResultRow) -> User = {
            User(
                loginName = it[T.loginName],
                nickName = it[T.nickName],
                password = it[T.password]
            )
        }
    }

    override fun findByLoginName(loginName: String): User? =
        T
            .slice(T.columns)
            .select { T.loginName eq loginName }
            .map(MAPPER)
            .firstOrNull()
}