package com.kaiqkt.auth.domain.repositories

import com.kaiqkt.auth.domain.models.Session
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface SessionRepository: JpaRepository<Session, Long> {
    @Query("SELECT s FROM Session s " +
            "WHERE s.refreshToken = :refreshToken AND " +
            "s.expireAt > CURRENT_TIMESTAMP AND s.revokedAt IS NULL")
    fun findValidByRefreshToken(refreshToken: String): Session?

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Session s " +
            "WHERE s.id = :id AND " +
            "s.expireAt > CURRENT_TIMESTAMP AND s.revokedAt IS NULL")
    fun existsValidById(id: String): Boolean

    @Transactional
    @Modifying
    @Query("UPDATE Session s SET s.revokedAt = CURRENT_TIMESTAMP WHERE s.id = :id AND s.user.id = :userId")
    fun revoke(id: String, userId: String)

    @Transactional
    @Modifying
    @Query("UPDATE Session s SET s.revokedAt = CURRENT_TIMESTAMP WHERE s.user.id = :userId")
    fun revokeAllByUserId(userId: String)

    @Transactional
    @Modifying
    @Query("UPDATE Session s SET s.revokedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    fun revokeAllByIds(ids: List<String>)

    @Query("SELECT s FROM Session s WHERE :id IS NULL OR s.id = :id OR s.user.id = :id")
    fun findAllByIdOrUserId(id: String?, pageable: Pageable?): Page<Session>
}
