package com.kaiqkt.auth.unit.domain.models

import com.kaiqkt.auth.domain.models.Role

object RoleSampler {
    fun sample(name: String? = null) = Role(
        name = name ?: "ADMIN",
        description = "Administrator"
    )
}
