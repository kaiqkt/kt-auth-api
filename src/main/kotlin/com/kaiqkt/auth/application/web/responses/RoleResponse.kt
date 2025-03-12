package com.kaiqkt.auth.application.web.responses

import com.kaiqkt.auth.domain.models.Role
import com.kaiqkt.auth.generated.application.web.dtos.RoleResponseV1

fun Role.toV1() = RoleResponseV1(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt.toString(),
)
