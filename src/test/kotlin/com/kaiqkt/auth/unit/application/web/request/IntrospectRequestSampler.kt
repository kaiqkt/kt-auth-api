package com.kaiqkt.auth.unit.application.web.request

import com.kaiqkt.auth.generated.application.web.dtos.IntrospectRequestV1
import com.kaiqkt.kt.tools.security.utils.TokenUtils

object IntrospectRequestSampler {
    fun sample() = IntrospectRequestV1(
        accessToken = "Bearer ${
            TokenUtils.generateJwt(
                mapOf("string" to "string"),
                30L,
                "secret"
            )
        }"
    )
}
