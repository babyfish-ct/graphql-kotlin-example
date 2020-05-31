package com.citicguoan.training.model

import com.expediagroup.graphql.annotations.GraphQLIgnore

class User(
    val loginName: String,
    val nickName: String,
    @GraphQLIgnore val password: ByteArray
)