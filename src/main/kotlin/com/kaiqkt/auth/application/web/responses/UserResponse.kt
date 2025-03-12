package com.kaiqkt.auth.application.web.responses

import com.kaiqkt.auth.domain.models.User
import com.kaiqkt.auth.generated.application.web.dtos.UserResponseV1

fun User.toV1() = UserResponseV1(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    isVerified = isVerified,
    roles = roles.map { it.toV1() },
    createdAt = createdAt.toString(),
    updatedAt = updatedAt?.toString()
)
