package com.kaiqkt.auth.unit.domain.dtos

import com.kaiqkt.auth.domain.dtos.Introspect

object IntrospectSampler {
    fun sample() = Introspect(
        active = true,
        username = "username",
        exp = 1234567890,
        sub = "sub",
        roles = listOf("ROLE_USER")
    )
}
