package com.kaiqkt.auth.domain.repositories

import com.kaiqkt.auth.domain.models.Verification
import com.kaiqkt.auth.domain.models.enums.VerificationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface VerificationRepository : JpaRepository<Verification, String> {
    @Query(
        "SELECT v FROM Verification v " +
                "WHERE v.user.id = :userId AND v.type = :type AND v.expireAt > CURRENT_TIMESTAMP " +
                "ORDER BY v.createdAt DESC"
    )
    fun findAllByUserIdAndType(userId: String, type: VerificationType): List<Verification>

    @Query(
        "SELECT v FROM Verification v " +
                "WHERE v.code = :code AND v.expireAt > CURRENT_TIMESTAMP"
    )
    fun findByCode(code: String): Verification?
}
