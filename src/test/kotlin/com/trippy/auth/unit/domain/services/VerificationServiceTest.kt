package com.trippy.auth.unit.domain.services

import com.trippy.auth.domain.exceptions.DomainException
import com.trippy.auth.domain.exceptions.ErrorType
import com.trippy.auth.domain.gateways.MailGateway
import com.trippy.auth.domain.models.enums.VerificationType
import com.trippy.auth.domain.repositories.VerificationRepository
import com.trippy.auth.domain.services.VerificationService
import com.trippy.auth.unit.domain.models.UserSampler
import com.trippy.auth.unit.domain.models.VerificationSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class VerificationServiceTest {
    private val verificationRepository: VerificationRepository = mockk()
    private val mailGateway: MailGateway = mockk()
    private val verificationService = VerificationService(
        verificationRepository,
        mailGateway,
        "http://localhost:8080"
    )

    @Test
    fun `given a user should create a new verification and send an email`() = runBlocking {
        val user = UserSampler.sample()

        every { verificationRepository.findByUserIdAndType(any(), any()) } returns null
        justRun { mailGateway.send(any()) }
        every { verificationRepository.save(any()) } returns VerificationSampler.sample()

        verificationService.send(user, VerificationType.EMAIL)

        verify { verificationRepository.save(any()) }
        verify { mailGateway.send(any()) }
        verify { verificationRepository.findByUserIdAndType(user.id, VerificationType.EMAIL) }
    }

    @Test
    fun `given a user PASSWORD should create a new verification and send an email`() = runBlocking {
        val user = UserSampler.sample()

        every { verificationRepository.findByUserIdAndType(any(), any()) } returns null
        justRun { mailGateway.send(any()) }
        every { verificationRepository.save(any()) } returns VerificationSampler.sample()

        verificationService.send(user, VerificationType.PASSWORD)

        verify { verificationRepository.save(any()) }
        verify { mailGateway.send(any()) }
        verify { verificationRepository.findByUserIdAndType(user.id, VerificationType.PASSWORD) }
    }

    @Test
    fun `given a user when spam time is not passed should not create a new verification`() = runBlocking {
        val user = UserSampler.sample()

        every { verificationRepository.findByUserIdAndType(any(), any()) } returns VerificationSampler.sample()

        verificationService.send(user, VerificationType.EMAIL)

        verify(exactly = 0) { verificationRepository.save(any()) }
        verify(exactly = 0) { mailGateway.send(any()) }
        verify { verificationRepository.findByUserIdAndType(user.id, VerificationType.EMAIL) }
    }

    @Test
    fun `given a user should create a new verification and send an email if spam time has passed`() = runBlocking {
        val user = UserSampler.sample()

        every { verificationRepository.findByUserIdAndType(any(), any()) } returns VerificationSampler.sample()
            .copy(createdAt = LocalDateTime.now().plusMinutes(-10))
        justRun { mailGateway.send(any()) }
        every { verificationRepository.save(any()) } returns VerificationSampler.sample()

        verificationService.send(user, VerificationType.EMAIL)

        verify { verificationRepository.save(any()) }
        verify { mailGateway.send(any()) }
        verify { verificationRepository.findByUserIdAndType(user.id, VerificationType.EMAIL) }
    }

    @Test
    fun `given a code should return a verification`() {
        val code = "123456"

        every { verificationRepository.findByCode(code) } returns VerificationSampler.sample()

        verificationService.findByCode(code)

        verify { verificationRepository.findByCode(code) }
    }

    @Test
    fun `given a code when not exist should throw an exception`() {
        val code = "123456"

        every { verificationRepository.findByCode(code) } returns null

        val exception = assertThrows<DomainException> { verificationService.findByCode(code) }


        verify { verificationRepository.findByCode(code) }

        assertEquals(exception.type, ErrorType.INVALID_VERIFICATION_CODE)
    }

    @Test
    fun `given a verification should delete it`() {
        val verification = VerificationSampler.sample()

        justRun { verificationRepository.delete(verification) }

        verificationService.delete(verification)

        verify { verificationRepository.delete(verification) }
    }
}