package com.kaiqkt.auth.domain.models

import io.azam.ulidj.ULID
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener::class)
data class Role(
    @Id
    val id: String = ULID.random(),
    var name: String = "",
    val description: String? = null,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null
) {
    init {
        if (!name.startsWith("ROLE_")) {
            name = "ROLE_$name".uppercase()
        }
    }
}
