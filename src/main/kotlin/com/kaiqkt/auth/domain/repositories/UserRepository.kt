package com.kaiqkt.auth.domain.repositories

import com.kaiqkt.auth.domain.models.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository: JpaRepository<User, String> {
    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): User?

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isVerified = true WHERE u.id = :userId")
    fun updateIsVerified(userId: String)

    @Query(
        "SELECT u FROM User u WHERE :query IS NULL OR " +
                "u.id = :query OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))"
    )
    fun findAllByQuery(query: String?, pageable: PageRequest): Page<User>
}
