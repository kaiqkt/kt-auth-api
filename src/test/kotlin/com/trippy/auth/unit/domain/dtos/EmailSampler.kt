package com.trippy.auth.unit.domain.dtos

import com.trippy.auth.domain.dtos.Email
import com.trippy.auth.unit.domain.models.UserSampler
import java.util.UUID

object EmailSampler {
    fun sampleVerifyEmail() =Email.VerifyEmail(
        user = UserSampler.sample(),
        redirectLink = "http://localhost:8080/${UUID.randomUUID()}/verify-email"
    )
}