package com.trippy.auth.unit.domain.models

import com.trippy.auth.domain.models.Role

object RoleSampler {
    fun sample(name: String? = null) = Role(
        name = name ?: "ADMIN",
        description = "Administrator"
    )
}