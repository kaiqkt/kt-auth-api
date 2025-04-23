package com.kaiqkt.auth.integration.web

import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.generated.application.web.dtos.AuthenticationResponseV1
import com.kaiqkt.auth.generated.application.web.dtos.ErrorV1
import com.kaiqkt.auth.generated.application.web.dtos.LoginRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.VerifiedSessionResponseV1
import com.kaiqkt.auth.integration.IntegrationTest
import com.kaiqkt.auth.integration.utils.SpringMailMock
import com.kaiqkt.auth.unit.domain.models.SessionSampler
import com.kaiqkt.auth.unit.domain.models.UserSampler
import io.restassured.RestAssured.given
import org.awaitility.Awaitility.await
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class AuthenticationIntegrationTest : IntegrationTest() {

    @Test
    fun `given a login request when the data match should return the tokens and create a new session`() {
        val user = userRepository.save(UserSampler.sample())
        val request = LoginRequestV1(email = user.email, password = "Password@123")

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_api_auth_v1+json")
            .body(request)
            .post("/login")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val authentication = mapper.readValue(response.body.asString(), AuthenticationResponseV1::class.java)

        val session = sessionRepository.findAll().first()

        assertEquals(session.refreshToken, authentication.refreshToken)
    }

    @Test
    fun `given a login request when the user is not verified should return http 401 and send verification email`() {
        val user = userRepository.save(UserSampler.sample().copy(isVerified = false))
        val request = LoginRequestV1(email = user.email, password = "Password@123")

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_api_auth_v1+json")
            .body(request)
            .post("/login")
            .then()
            .statusCode(401)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        await().atMost(2, TimeUnit.SECONDS)
            .untilAsserted {
                assertEquals(ErrorType.EMAIL_NOT_VERIFIED.name, error.type)
                assertEquals(1, verificationRepository.count())
                assertTrue(SpringMailMock.verify())
            }
    }

    @Test
    fun `given a email and a password when the password dont match should return http status 401`() {
        val user = userRepository.save(UserSampler.sample())
        val request = LoginRequestV1(email = user.email, password = "Password@12")

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_api_auth_v1+json")
            .body(request)
            .post("/login")
            .then()
            .statusCode(401)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_CREDENTIAL.name, error.type)
    }

    @Test
    fun `given a empty email should return http status 400`() {
        val request = LoginRequestV1(email = "", password = "password")

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_api_auth_v1+json")
            .body(request)
            .post("/login")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals("email must not be empty", error.errors!!["email"])
    }

    @Test
    fun `given a empty password should return http status 400`() {
        val request = LoginRequestV1(email = "email@email", password = "")

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_api_auth_v1+json")
            .body(request)
            .post("/login")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals("password must not be empty", error.errors!!["password"])
    }

    @Test
    fun `given a refresh token when the session is not found should return http status 401`() {
        val response = given()
            .header("X-Refresh-Token", "refreshToken")
            .post("/refresh")
            .then()
            .statusCode(401)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_SESSION.name, error.type)

    }

    @Test
    fun `given a refresh token when the session is found should return the tokens and create a new session`() {
        val user = userRepository.save(UserSampler.sample())
        val session = sessionRepository.save(SessionSampler.sample().copy(user = user))

        val response = given()
            .header("X-Refresh-Token", session.refreshToken)
            .post("/refresh")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val authentication = mapper.readValue(response.body.asString(), AuthenticationResponseV1::class.java)

        val newSession = sessionRepository.findAll().last()

        assertEquals(newSession.refreshToken, authentication.refreshToken)
    }

    @Test
    fun `given a session id when the session is found should return http status 200`() {
        given()
            .header("Authorization", "Bearer ${mockAuthentication().first}")
            .post("/verify")
            .then()
            .statusCode(204)
            .extract()
            .response()
    }
}
