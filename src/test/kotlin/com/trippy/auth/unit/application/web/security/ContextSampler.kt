package com.trippy.auth.unit.application.web.security

import com.trippy.auth.domain.utils.TokenUtils
import com.kaiqkt.springtools.security.dto.Authentication

object ContextSampler {

    fun sample(data: Map<String, Any>? = null): Authentication {
        val token = TokenUtils.generateJwt(
            data ?: mapOf("session_id" to "sessionId", "user_id" to "userId", "roles" to listOf("ROLE_USER")),
            15, "secret"
        )

        return Authentication(HashMap<String, Any>(TokenUtils.verifyJwt(token, "secret").claims), token)
    }
}