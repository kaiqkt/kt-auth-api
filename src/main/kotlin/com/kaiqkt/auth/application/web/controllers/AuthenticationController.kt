package com.kaiqkt.auth.application.web.controllers

import com.kaiqkt.auth.application.web.responses.toV1
import com.kaiqkt.auth.domain.services.AuthenticationService
import com.kaiqkt.auth.generated.application.web.controllers.IntrospectApi
import com.kaiqkt.auth.generated.application.web.controllers.LoginApi
import com.kaiqkt.auth.generated.application.web.controllers.RefreshApi
import com.kaiqkt.auth.generated.application.web.dtos.AuthenticationResponseV1
import com.kaiqkt.auth.generated.application.web.dtos.IntrospectRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.IntrospectResponseV1
import com.kaiqkt.auth.generated.application.web.dtos.LoginRequestV1
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController(
    private val authenticationService: AuthenticationService
) : LoginApi, RefreshApi, IntrospectApi {

    override fun login(
        xForwardedFor: String,
        loginRequestV1: LoginRequestV1
    ): ResponseEntity<AuthenticationResponseV1> {
        val authentication = authenticationService.authenticate(
            loginRequestV1.email,
            loginRequestV1.password,
            xForwardedFor
        )

        return ResponseEntity.ok(
            AuthenticationResponseV1(
                accessToken = authentication.first,
                refreshToken = authentication.second
            )
        )
    }

    override fun refresh(
        xForwardedFor: String,
        xRefreshToken: String
    ): ResponseEntity<AuthenticationResponseV1> {
        val authentication = authenticationService.refresh(xRefreshToken, xForwardedFor)

        return ResponseEntity.ok(
            AuthenticationResponseV1(
                accessToken = authentication.first,
                refreshToken = authentication.second
            )
        )
    }

    @PreAuthorize("hasRole('ROLE_API')")
    override fun introspect(introspectRequestV1: IntrospectRequestV1): ResponseEntity<IntrospectResponseV1> {
        val accessToken = introspectRequestV1.accessToken.substringAfter("Bearer")
        val response = authenticationService.introspect(accessToken)

        return ResponseEntity.ok(response.toV1())
    }
}
