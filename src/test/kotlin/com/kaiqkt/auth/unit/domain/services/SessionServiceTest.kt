package com.kaiqkt.auth.unit.domain.services

import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.domain.repositories.SessionRepository
import com.kaiqkt.auth.domain.services.SessionService
import com.kaiqkt.auth.unit.domain.models.SessionSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "expireAt")

        every { sessionRepository.findAllByIdOrUserId(any(), any()) } returns PageImpl(listOf(SessionSampler.sample()))

        sessionService.findAll("id", pageRequest)

        verify { sessionRepository.findAllByIdOrUserId("id", any()) }
    }

    @Test
    fun `given an id and a pagination when sort property should throw a DomainException`() {
        val pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "name")

        val exception = assertThrows<DomainException> {
            sessionService.findAll("id", pageRequest)
        }

        assertEquals(exception.type, ErrorType.INVALID_SORT_PROPERTY)
    }

    @Test
    fun `given a session id when exists should return successfully`() {
        every { sessionRepository.findById(any()) } returns Optional.of(SessionSampler.sample())

        sessionService.findById("id")

        verify { sessionRepository.findById("id") }
    }

    @Test
    fun `given a session id when not exists should throws a exception`() {
        every { sessionRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> {
            sessionService.findById("id")
        }

        verify { sessionRepository.findById("id") }
        assertEquals(exception.type, ErrorType.INVALID_SESSION)
    }
}
