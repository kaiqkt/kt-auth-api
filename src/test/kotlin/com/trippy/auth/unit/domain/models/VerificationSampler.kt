package com.trippy.auth.unit.domain.models

import com.trippy.auth.domain.models.Verification

object VerificationSampler {
    fun sample() = Verification(
        user = UserSampler.sample(),
        code = "code"
    )
}