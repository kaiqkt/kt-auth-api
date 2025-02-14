package com.trippy.auth.domain.services

import com.trippy.auth.domain.dtos.Email
import com.trippy.auth.domain.exceptions.DomainException
import com.trippy.auth.domain.exceptions.ErrorType
import com.trippy.auth.domain.gateways.MailGateway
import com.trippy.auth.domain.models.User
import com.trippy.auth.domain.models.Verification
import com.trippy.auth.domain.models.enums.VerificationType
import com.trippy.auth.domain.repositories.VerificationRepository
import com.trippy.auth.domain.utils.Constants
import com.trippy.auth.domain.utils.TokenUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class VerificationService(
    private val verificationRepository: VerificationRepository,
    private val mailGateway: MailGateway,
    @Value("\${service-url}")
    private val serviceUrl: String
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun send(user: User, type: VerificationType) {
        var verification = verificationRepository.findAllByUserIdAndType(user.id, type).firstOrNull()

        if (verification != null
            && verification.createdAt.plusMinutes(Constants.EMAIL_VERIFICATION_SPAM_DELAY) > LocalDateTime.now()
        ) {
            log.info("Verification code already sent to user ${user.id}")
            return
        }

        verification = Verification(
            user = user,
            code = UUID.randomUUID().toString(),
            type = type
        )

        val email = getEmail(verification)
        verificationRepository.save(verification)
        mailGateway.send(email)

        log.info("Verification code sent to user ${user.id}")
    }

    fun findByCode(code: String): Verification {
        return verificationRepository.findByCode(code)
            ?: throw DomainException(ErrorType.INVALID_VERIFICATION_CODE)
    }

    fun delete(verification: Verification) {
        verificationRepository.delete(verification)
        log.info("Verification ${verification.id} deleted")
    }

    private fun getEmail(verification: Verification): Email {
        return when (verification.type) {
            VerificationType.EMAIL -> Email.VerifyEmail(
                user = verification.user,
                redirectLink = "$serviceUrl/user/verify-email?code=${verification.code}"
            )

            else -> Email.ResetPassword(
                user = verification.user,
                redirectLink = "$serviceUrl/user/${verification.code}/reset-password"
            )
        }
    }
}