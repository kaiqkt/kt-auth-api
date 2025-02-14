package com.trippy.auth.unit.domain.services

import com.trippy.auth.domain.repositories.SessionRepository
import com.trippy.auth.domain.services.SessionService
import com.trippy.auth.unit.domain.models.SessionSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import kotlin.test.Test

class SessionServiceTest {
    private val sessionRepository: SessionRepository = mockk()
    private val sessionService = SessionService(sessionRepository)

    @Test
    fun `given a session should save successfully`() {
        every { sessionRepository.save(any()) } returns SessionSampler.sample()

        sessionService.save(SessionSampler.sample())

        verify { sessionRepository.save(any()) }
    }

    @Test
    fun `given a refresh token should find a valid session`() {
        every { sessionRepository.findValidByRefreshToken(any()) } returns SessionSampler.sample()

        sessionService.findValidByRefreshToken("refresh_token")

        verify { sessionRepository.findValidByRefreshToken("refresh_token") }
    }

    @Test
    fun `given an id should check if a valid session exists`() {
        every { sessionRepository.existsValidById(any()) } returns true

        sessionService.existsValidById("id")

        verify { sessionRepository.existsValidById("id") }
    }

    @Test
    fun `given an id and a user id should revoke a session successfully`() {
        justRun { sessionRepository.revoke(any(), any()) }

        sessionService.revokeById("id", "user_id")

        verify { sessionRepository.revoke("id", "user_id") }
    }

    @Test
    fun `given an user id should revoke all sessions successfully`() {
        justRun { sessionRepository.revokeAllByUserId(any()) }

        sessionService.revokeAllByUserId("user_id")

        verify { sessionRepository.revokeAllByUserId("user_id") }
    }

    @Test
    fun `given a list of ids should revoke all sessions successfully`() {
        justRun { sessionRepository.revokeAllByIds(any()) }

        sessionService.revokeAllByIds(listOf("id1", "id2"))

        verify { sessionRepository.revokeAllByIds(listOf("id1", "id2")) }
    }

    @Test
    fun `given an id and a pagination should search sessions successfully`() {
        val pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "name")

        every { sessionRepository.findAllByIdOrUserId(any(), any()) } returns PageImpl(listOf(SessionSampler.sample()))

        sessionService.search("id", pageRequest)

        verify { sessionRepository.findAllByIdOrUserId("id", any()) }
    }
}