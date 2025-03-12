package com.kaiqkt.auth.integration.web

import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.generated.application.web.dtos.ErrorV1
import com.kaiqkt.auth.generated.application.web.dtos.IdsRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.PageResponseV1
import com.kaiqkt.auth.integration.IntegrationTest
import io.restassured.RestAssured.given
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SessionIntegrationTest : IntegrationTest() {

    @Test
    fun `given a session id when the session is valid should revoke successfully`() {
        val authentication = mockAuthentication()
        val session = sessionRepository.findAll().first()

        given()
            .header("Authorization", "Bearer ${authentication.first}")
            .patch("/session/${session.id}/revoke")
            .then()
            .statusCode(200)
            .extract()
            .response()

        assertNotNull(sessionRepository.findAll().first().revokedAt)
    }

    @Test
    fun `given a access token should revoke the current session successfully`() {
        val authentication = mockAuthentication()

        given()
            .header("Authorization", "Bearer ${authentication.first}")
            .patch("/session/revoke/current")
            .then()
            .statusCode(200)
            .extract()
            .response()

        assertNotNull(sessionRepository.findAll().first().revokedAt)
    }

    @Test
    fun `given a access token should revoke all sessions successfully`() {
        val authentication = mockAuthentication()

        given()
            .header("Authorization", "Bearer ${authentication.first}")
            .patch("/sessions/revoke")
            .then()
            .statusCode(200)
            .extract()
            .response()

        assertNotNull(sessionRepository.findAll().first().revokedAt)
    }

    @Test
    fun `given a list of session ids should revoke all sessions successfully`() {
        val authentication = mockAuthentication()
        val sessions = sessionRepository.findAll().first()
        val request = IdsRequestV1(listOf(sessions.id))

        given()
            .header("Authorization", "Bearer ${authentication.first}")
            .contentType("application/vnd.kaiqkt_auth_api_session_v1+json")
            .body(request)
            .patch("/sessions/revoke/all")
            .then()
            .statusCode(200)
            .extract()
            .response()

        assertNotNull(sessionRepository.findAll().first().revokedAt)
    }

    @Test
    fun `given a request to find all sessions when findAll is called by the userId should return a page of sessions`() {
        val authentication = mockAuthentication()
        val userId = userRepository.findAll().first().id

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/sessions?id=$userId")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val pageResponse = mapper.readValue(response.body.asString(), PageResponseV1::class.java)

        assertEquals(pageResponse.totalElements, 1)
    }

    @Test
    fun `given a request to find all sessions when findAll is called by the id should return a page of sessions`() {
        val authentication = mockAuthentication()
        val sessionId = sessionRepository.findAll().first().id

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/sessions?id=$sessionId")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val pageResponse = mapper.readValue(response.body.asString(), PageResponseV1::class.java)

        assertEquals(pageResponse.totalElements, 1)
    }

    @Test
    fun `given a request to find all sessions when page size is more than 999 should return a error`() {
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/sessions?page=0&size=1000")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals(error.errors?.get("size"), "must be less than or equal to 999")
    }
}
