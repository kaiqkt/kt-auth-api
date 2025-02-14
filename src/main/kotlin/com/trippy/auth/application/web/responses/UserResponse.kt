package com.trippy.auth.application.web.responses

import com.trippy.auth.domain.models.User
import com.trippy.auth.generated.application.web.dtos.UserResponseV1

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