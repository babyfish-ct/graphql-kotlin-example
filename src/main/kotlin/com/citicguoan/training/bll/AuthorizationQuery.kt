package com.citicguoan.training.bll

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.citicguoan.training.bll.exception.illegalLoginName
import com.citicguoan.training.bll.exception.illegalPassword
import com.citicguoan.training.dal.UserRepository
import com.citicguoan.training.model.User
import com.citicguoan.training.model.result.LoginResult
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.util.*
import com.auth0.jwt.exceptions.JWTVerificationException
import com.citicguoan.training.bll.exception.unauthorized


@Component
open class AuthorizationQuery(
    private val userRepository: UserRepository
) : Query {

    companion object {
        private val messageDigest: MessageDigest =
            MessageDigest.getInstance("SHA")

    }

    @Transactional(readOnly = true)
    open fun login(loginName: String, password: String): LoginResult {
        val user = userRepository.findByLoginName(loginName) ?: illegalLoginName(loginName)
        val password = messageDigest.digest(password.toByteArray())
        if (!Arrays.equals(password, user.password)) {
            illegalPassword()
        }
        val token=
            JWT
                .create()
                .withAudience(user.loginName)
                .sign(Algorithm.HMAC256(password))
        return LoginResult(user, token)
    }

    @Transactional
    open fun user(token: String): User =
        JWT.decode(token).audience[0].let {
            val user = userRepository.findByLoginName(it)
            if (user === null) {
                unauthorized()
            }
            try {
                JWT.require(Algorithm.HMAC256(user.password)).build().verify(token)
            } catch (ex: JWTVerificationException) {
                unauthorized()
            }
            return user
        }
}