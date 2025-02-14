package com.trippy.auth.unit.domain.dtos

object AuthenticationSampler {
    fun sample(): Pair<String, String> {
        return Pair("accessToken", "refreshToken")
    }
}