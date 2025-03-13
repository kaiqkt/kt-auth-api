package com.kaiqkt.auth.domain.services

import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.domain.models.Session
import com.kaiqkt.auth.domain.models.enums.VerificationType
import com.kaiqkt.auth.domain.utils.AuthenticationProperties
import com.kaiqkt.auth.domain.utils.TokenUtils
import jakarta.transaction.Transactional
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthenticationService(
    private val userService: UserService,
    private val sessionService: SessionService,
    private val authenticationProperties: AuthenticationProperties,
    private val verificationService: VerificationService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun authenticate(email: String, password: String, ip: String?): Pair<String, String> {
        val user = userService.findByEmail(email)

        if (!BCrypt.checkpw(password, user.credential.hash)) {
            throw DomainException(ErrorType.INVALID_CREDENTIAL)
        }

        if (!user.isVerified) {
            CoroutineScope(Dispatchers.IO).launch {
                verificationService.send(user, VerificationType.EMAIL)
            }
            throw DomainException(ErrorType.EMAIL_NOT_VERIFIED)
        }

        val expireAt = LocalDateTime.now().plusDays(authenticationProperties.refreshTokenExpiration.toLong())
        var session = Session(ip = ip, user = user, expireAt = expireAt)
        val refreshToken = TokenUtils.generateHash(session.id, authenticationProperties.refreshTokenSecret)

        session = sessionService.save(session.copy(refreshToken = refreshToken))

        return generateAuthentication(session).also {
            log.info("User {} authenticated successfully", user.id)
        }
    }

    @Transactional
    fun refresh(refreshToken: String, ip: String?): Pair<String, String> {
        var session = sessionService.findValidByRefreshToken(refreshToken)
            ?: throw DomainException(ErrorType.INVALID_SESSION)

        session = session.copy(
            ip = ip,
            expireAt = LocalDateTime.now().plusDays(authenticationProperties.refreshTokenExpiration.toLong()),
            refreshToken = TokenUtils.generateHash(session.id, authenticationProperties.refreshTokenSecret)
        ).apply {
            sessionService.save(this)
        }

        return generateAuthentication(session).also {
            log.info("Session {} refreshed successfully", session.id)
        }
    }

    fun verify(sessionId: String): Boolean {
        return sessionService.existsValidById(sessionId)
    }

    private fun generateAuthentication(session: Session): Pair<String, String> {

        val accessToken = TokenUtils.generateJwt(
            mapOf(
                "user_id" to session.user.id,
                "session_id" to session.id,
                "roles" to session.user.roles.map { it.name }),
            authenticationProperties.accessTokenExpiration,
            authenticationProperties.accessTokenSecret
        )

        return Pair(accessToken, session.refreshToken)
    }
}
