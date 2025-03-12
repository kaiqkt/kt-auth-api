package com.kaiqkt.auth.domain.models

import io.azam.ulidj.ULID
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "credentials")
@EntityListeners(AuditingEntityListener::class)
data class Credential(
    @Id
    val id: String = ULID.random(),
    val hash: String = "",
    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null
){
    override fun toString(): String {
        return "Credential(id='$id', hash='****', createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
