package com.kaiqkt.auth.domain.services

import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.domain.models.Session
import com.kaiqkt.auth.domain.repositories.SessionRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class SessionService(
    private val sessionRepository: SessionRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        val ALLOWED_SORT_COLUMNS = setOf("expireAt", "createdAt", "updatedAt", "expireAt", "revokedAt")
    }

    fun save(session: Session): Session {
        return sessionRepository.save(session).also {
            log.info("Session ${session.id} for user ${session.user.id} saved successfully")
        }
    }

    fun findValidByRefreshToken(refreshToken: String): Session? {
        return sessionRepository.findValidByRefreshToken(refreshToken)
    }

    fun findById(id: String): Session {
        return sessionRepository.findById(id).getOrNull()
            ?: throw DomainException(ErrorType.INVALID_SESSION)
    }

    fun existsValidById(id: String): Boolean {
        return sessionRepository.existsValidById(id)
    }

    fun revokeById(id: String, userId: String) {
        sessionRepository.revoke(id, userId)
        log.info("Session $id for user $userId revoked successfully")
    }

    fun revokeAllByUserId(userId: String) {
        sessionRepository.revokeAllByUserId(userId)
        log.info("All sessions for user $userId revoked successfully")
    }

    fun revokeAllByIds(ids: List<String>) {
        sessionRepository.revokeAllByIds(ids)
        log.info("Sessions ${ids.joinToString()} revoked successfully")
    }

    fun findAll(id: String?, pageRequest: PageRequest): Page<Session> {
        val invalidProperty = pageRequest.sort
            .map { it.property }
            .firstOrNull { it !in ALLOWED_SORT_COLUMNS }

        if (invalidProperty != null) {
            throw DomainException(ErrorType.INVALID_SORT_PROPERTY)
        }

        return sessionRepository.findAllByIdOrUserId(id, pageRequest)
    }
}
