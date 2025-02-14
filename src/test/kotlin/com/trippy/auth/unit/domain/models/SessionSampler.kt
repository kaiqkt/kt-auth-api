package com.trippy.auth.unit.domain.models

import com.trippy.auth.domain.models.Session
import java.time.LocalDateTime

object SessionSampler {
    fun sample(): Session = Session(
        ip = "192.0.0.1",
        user = UserSampler.sample(),
        expireAt = LocalDateTime.now().plusDays(1),
        refreshToken = "refreshToken"
    )
}