package com.trippy.auth.application.web.requests

import com.trippy.auth.domain.models.Role
import com.trippy.auth.generated.application.web.dtos.RoleRequestV1

fun RoleRequestV1.toDomain() = Role(
    name = name,
    description = description
)