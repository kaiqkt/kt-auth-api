package com.trippy.auth.application.web.responses

import com.trippy.auth.domain.models.Role
import com.trippy.auth.generated.application.web.dtos.RoleResponseV1

fun Role.toV1() = RoleResponseV1(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt.toString(),
)