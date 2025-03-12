package com.kaiqkt.auth.unit.application.web.controllers

import com.kaiqkt.auth.application.web.controllers.SessionController
import com.kaiqkt.auth.domain.services.SessionService
import com.kaiqkt.auth.generated.application.web.dtos.IdsRequestV1
import com.kaiqkt.auth.unit.application.web.security.ContextSampler
import com.kaiqkt.auth.unit.domain.models.SessionSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionControllerTest {
    private val sessionService = mockk<SessionService>()
    private val controller = SessionController(sessionService)

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.getContext().authentication = ContextSampler.sample()
    }

    @Test
    fun `given a session id and user when revoke should return ok`() {
        justRun { sessionService.revokeById(any(), any()) }

        val response = controller.revoke("sessionId")

        verify { sessionService.revokeById("sessionId", "userId") }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a access token when get the session id and the user id should revoke successfully the current`() {
        justRun { sessionService.revokeById(any(), any()) }

        val response = controller.revokeCurrent()

        verify { sessionService.revokeById("sessionId", "userId") }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a session id when revoke all should return ok`() {
        justRun { sessionService.revokeAllByUserId(any()) }

        val response = controller.revokeAll()

        verify { sessionService.revokeAllByUserId("userId") }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a list of session ids when revoke all by ids should return ok`() {
        justRun { sessionService.revokeAllByIds(any()) }

        val response = controller.revokeAllByIds(IdsRequestV1(listOf("sessionId")))

        verify { sessionService.revokeAllByIds(listOf("sessionId")) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a request to find all sessions when is a user calling should return just his sessions`() {
        val sessions = listOf(SessionSampler.sample())
        val page = PageImpl(sessions)

        every { sessionService.search(any(), any()) } returns page

        val response = controller.getAll(0, 10, "ASC", "createdBy", "userId")

        verify { sessionService.search("userId", any()) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a request to find all sessions when is a admin calling should return all sessions needed`() {
        SecurityContextHolder.getContext().authentication = ContextSampler.sample(
            mapOf("session_id" to "sessionId", "user_id" to "userId", "roles" to listOf("ROLE_ADMIN"))
        )

        val sessions = listOf(SessionSampler.sample())
        val page = PageImpl(sessions)

        every { sessionService.search(any(), any()) } returns page

        val response = controller.getAll(0, 10, "ASC", "createdBy", "userId")

        verify { sessionService.search("userId", any()) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }
}
