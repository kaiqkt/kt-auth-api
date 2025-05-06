package com.kaiqkt.auth.domain.dtos

data class Introspect(
    val active: Boolean,
    val username: String,
    val exp: Long,
    val sub: String,
    val roles: List<String>
)
