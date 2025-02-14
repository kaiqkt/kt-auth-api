package com.trippy.auth.application.web.controllers

import com.kaiqkt.springtools.security.utils.Context
import com.trippy.auth.domain.services.AuthenticationService
import com.trippy.auth.domain.utils.Constants
import com.trippy.auth.generated.application.web.controllers.LoginApi
import com.trippy.auth.generated.application.web.controllers.RefreshApi
import com.trippy.auth.generated.application.web.controllers.VerifyApi
import com.trippy.auth.generated.application.web.dtos.AuthenticationResponseV1
import com.trippy.auth.generated.application.web.dtos.LoginRequestV1
import com.trippy.auth.generated.application.web.dtos.VerifiedSessionResponseV1
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController(
    private val authenticationService: AuthenticationService
) : LoginApi, RefreshApi, VerifyApi {

    override fun login(
        loginRequestV1: LoginRequestV1,
        xForwardedFor: String?
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
        xRefreshToken: String,
        xForwardedFor: String?
    ): ResponseEntity<AuthenticationResponseV1> {
        val authentication = authenticationService.refresh(xRefreshToken, xForwardedFor)

        return ResponseEntity.ok(
            AuthenticationResponseV1(
                accessToken = authentication.first,
                refreshToken = authentication.second
            )
        )
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    override fun verify(): ResponseEntity<VerifiedSessionResponseV1> {
        val sessionId = Context.getValue(Constants.SESSION_ID, String::class.java)
        val isVerified = authenticationService.verify(sessionId)

        return ResponseEntity.ok(VerifiedSessionResponseV1(isVerified))
    }
}