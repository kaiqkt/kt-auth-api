package com.kaiqkt.auth.unit.domain.dtos

import com.kaiqkt.auth.domain.dtos.Email
import com.kaiqkt.auth.unit.domain.models.UserSampler
import java.util.UUID

object EmailSampler {
    fun sampleVerifyEmail() =Email.VerifyEmail(
        user = UserSampler.sample(),
        redirectLink = "http://localhost:8080/${UUID.randomUUID()}/verify-email"
    )
}
