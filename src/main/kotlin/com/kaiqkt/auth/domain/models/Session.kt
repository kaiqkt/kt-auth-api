package com.kaiqkt.auth.domain.models

import io.azam.ulidj.ULID
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "sessions")
@EntityListeners(AuditingEntityListener::class)
data class Session(
    @Id
    val id: String = ULID.random(),
    val refreshToken: String = "",
    val ip: String? = null,
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User = User(),
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    val expireAt: LocalDateTime = LocalDateTime.now(),
    val revokedAt: LocalDateTime? = null
)
