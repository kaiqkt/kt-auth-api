package com.kaiqkt.auth.unit.domain.models

import com.kaiqkt.auth.domain.models.Verification

object VerificationSampler {
    fun sample() = Verification(
        user = UserSampler.sample(),
        code = "code"
    )
}
