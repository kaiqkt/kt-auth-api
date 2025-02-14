package com.trippy.auth.domain.repositories

import com.trippy.auth.domain.models.Credential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface CredentialRepository: JpaRepository<Credential, String> {
    @Modifying
    @Transactional
    @Query("UPDATE Credential c SET c.hash = :hash, c.updatedAt = CURRENT_TIMESTAMP WHERE c.user.id = :userId")
    fun updateHash(userId: String, hash: String)
}