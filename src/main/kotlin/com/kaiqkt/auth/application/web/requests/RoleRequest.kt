package com.kaiqkt.auth.application.web.requests

import com.kaiqkt.auth.domain.models.Role
import com.kaiqkt.auth.generated.application.web.dtos.RoleRequestV1

fun RoleRequestV1.toDomain() = Role(
    name = name,
    description = description
)
