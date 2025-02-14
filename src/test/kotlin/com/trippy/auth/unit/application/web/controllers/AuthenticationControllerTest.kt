package com.trippy.auth.unit.application.web.controllers

import com.trippy.auth.application.web.controllers.AuthenticationController
import com.trippy.auth.domain.services.AuthenticationService
import com.trippy.auth.generated.application.web.dtos.LoginRequestV1
import com.trippy.auth.unit.application.web.security.ContextSampler
import com.trippy.auth.unit.domain.dtos.AuthenticationSampler
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticationControllerTest {

    private val authenticationService: AuthenticationService = mockk()
    private val authenticationController: AuthenticationController = AuthenticationController(authenticationService)

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.getContext().authentication = ContextSampler.sample()
    }

    @Test
    fun `given a login request when authenticate should return authentication`() {
        val request = LoginRequestV1("test@email.com", "123456789")

        every { authenticationService.authenticate(any(), any(), any()) } returns AuthenticationSampler.sample()

        val response = authenticationController.login(request, "192.0.0.1")

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a refresh request when refresh should return authentication`() {
        every { authenticationService.refresh(any(), any()) } returns AuthenticationSampler.sample()

        val response = authenticationController.refresh("refreshToken", "192.0.0.1")

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a verify request when verify and the session is valid should return is_valid true`() {

        every { authenticationService.verify(any()) } returns true

        val response = authenticationController.verify()

        assertEquals(response.statusCode, HttpStatus.OK)
        assertEquals(response.body?.isValid, true)
    }

    @Test
    fun `given a verify request when verify and the session is not valid should return is_valid false`() {

        every { authenticationService.verify(any()) } returns false

        val response = authenticationController.verify()

        assertEquals(response.statusCode, HttpStatus.OK)
        assertEquals(response.body?.isValid, false)
    }
}