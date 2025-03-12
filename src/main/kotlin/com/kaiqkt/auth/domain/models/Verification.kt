package com.kaiqkt.auth.domain.models

import com.kaiqkt.auth.domain.models.enums.VerificationType
import io.azam.ulidj.ULID
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "verifications")
@EntityListeners(AuditingEntityListener::class)
data class Verification(
    @Id
    val id: String = ULID.random(),
    val code: String = "",
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User = User(),
    @Enumerated(EnumType.STRING)
    val type: VerificationType = VerificationType.EMAIL,
    val expireAt: LocalDateTime = LocalDateTime.now().plusDays(1),
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()
)
