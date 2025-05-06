package com.kaiqkt.auth.unit.domain.services

import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.domain.models.enums.VerificationType
import com.kaiqkt.auth.domain.services.AuthenticationService
import com.kaiqkt.auth.domain.services.SessionService
import com.kaiqkt.auth.domain.services.UserService
import com.kaiqkt.auth.domain.services.VerificationService
import com.kaiqkt.auth.domain.utils.AuthenticationProperties
import com.kaiqkt.auth.unit.domain.models.SessionSampler
import com.kaiqkt.auth.unit.domain.models.UserSampler
import com.kaiqkt.kt.tools.security.exception.AuthException
import com.kaiqkt.kt.tools.security.utils.TokenUtils
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.restassured.RestAssured.sessionId
import kotlinx.coroutines.runBlocking
import org.h2.schema.Domain
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthenticationServiceTest {
    private val userService: UserService = mockk()
    private val sessionService: SessionService = mockk()
    private val properties: AuthenticationProperties = AuthenticationProperties(
        accessTokenSecret = "secret",
        refreshTokenSecret = "secret",
        refreshTokenExpiration = 1,
        accessTokenExpiration = 1
    )
    private val verificationService: VerificationService = mockk()
    private val authenticationService =
        AuthenticationService(userService, sessionService, properties, verificationService)

    @Test
    fun `given a email and a password when the user is not found should throw DomainException`() {
        val email = "test@email.com"
        val password = "123456"

        every { userService.findByEmail(any()) } throws DomainException(ErrorType.USER_NOT_FOUND)

        val exception = assertThrows<DomainException> {
            authenticationService.authenticate(email, password, "192.0.0.1")
        }

        verify { userService.findByEmail(email) }

        assertEquals(ErrorType.USER_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a email and a password when the user is not verified should throw DomainException`() {
        val email = "test@email.com"
        val password = "Password@123"
        val user = UserSampler.sample().copy(isVerified = false)

        every { userService.findByEmail(any()) } returns user
        justRun { runBlocking { verificationService.send(any(), any()) } }

        val exception = assertThrows<DomainException> {
            authenticationService.authenticate(email, password, "192.0.0.1")
        }

        verify { userService.findByEmail(email) }
        verify { runBlocking { verificationService.send(user, VerificationType.EMAIL) } }

        assertEquals(ErrorType.EMAIL_NOT_VERIFIED, exception.type)
    }

    @Test
    fun `given a email and a password when the password dont match should throw DomainException`() {
        val email = "test@email.com"
        val password = "123456"

        every { userService.findByEmail(any()) } returns UserSampler.sample()

        val exception = assertThrows<DomainException> {
            authenticationService.authenticate(email, password, "192.0.0.1")
        }

        verify { userService.findByEmail(email) }

        assertEquals(ErrorType.INVALID_CREDENTIAL, exception.type)
    }

    @Test
    fun `given a email and a password when the password match should authentication successfully`() {
        val email = "test@email.com"
        val password = "Password@123"

        every { userService.findByEmail(any()) } returns UserSampler.sample()
        every { sessionService.save(any()) } returns SessionSampler.sample()

        authenticationService.authenticate(email, password, "192.0.0.1")

        verify { userService.findByEmail(email) }
        verify { sessionService.save(any()) }
    }

    @Test
    fun `given a refresh token when the session is not found should throw DomainException`() {

        every { sessionService.findValidByRefreshToken(any()) } returns null

        val exception = assertThrows<DomainException> {
            authenticationService.refresh("refreshToken", "127.0.0.1")
        }

        verify { sessionService.findValidByRefreshToken("refreshToken") }

        assertEquals(ErrorType.INVALID_SESSION, exception.type)
    }

    @Test
    fun `given a refresh token when the session is found should refresh the session successfully`() {
        val session = SessionSampler.sample()

        every { sessionService.findValidByRefreshToken(any()) } returns session
        every { sessionService.save(any()) } returns SessionSampler.sample()

        authenticationService.refresh("refreshToken", "192.0.0.1")

        verify { sessionService.findValidByRefreshToken("refreshToken") }
    }

    @Test
    fun `given a access token when the token is expired should throw a DomainException`() {
        val accessToken = TokenUtils.generateJwt(
            mapOf("session_id" to ULID.random()),
            -7,
            properties.accessTokenSecret
        )

        val exception = assertThrows<DomainException> { authenticationService.introspect(accessToken) }

        assertEquals(exception.type, ErrorType.INVALID_TOKEN)
    }


    @Test
    fun `given a valid access token when exist a session id and is not expired for him should return a session introspection successfully with active true`(){
        val accessToken = TokenUtils.generateJwt(
            mapOf("session_id" to ULID.random()),
            properties.accessTokenExpiration,
            properties.accessTokenSecret
        )

        every { sessionService.findById(any()) } returns SessionSampler.sample()

        val introspection = authenticationService.introspect(accessToken)

        verify { sessionService.findById(any()) }

        assertTrue { introspection.active }
    }

    @Test
    fun `given a valid access token when exist a session id and is expired for him should return a session introspection successfully with active false`(){
        val accessToken = TokenUtils.generateJwt(
            mapOf("session_id" to ULID.random()),
            properties.accessTokenExpiration,
            properties.accessTokenSecret
        )
        val session = SessionSampler.sample().copy(expireAt = LocalDateTime.now().minusDays(1))

        every { sessionService.findById(any()) } returns session

        val introspection = authenticationService.introspect(accessToken)

        verify { sessionService.findById(any()) }

        assertFalse { introspection.active }
    }
}
