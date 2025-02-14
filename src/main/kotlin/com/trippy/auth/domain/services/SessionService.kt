package com.trippy.auth.domain.services

import com.trippy.auth.domain.models.Session
import com.trippy.auth.domain.repositories.SessionRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SessionService(
    private val sessionRepository: SessionRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun save(session: Session): Session {
        return sessionRepository.save(session).also {
            log.info("Session ${session.id} for user ${session.user.id} saved successfully")
        }
    }

    fun findValidByRefreshToken(refreshToken: String): Session? {
        return sessionRepository.findValidByRefreshToken(refreshToken)
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

    fun search(id: String?, pageRequest: PageRequest): Page<Session> {
        return sessionRepository.findAllByIdOrUserId(id, pageRequest)
    }
}