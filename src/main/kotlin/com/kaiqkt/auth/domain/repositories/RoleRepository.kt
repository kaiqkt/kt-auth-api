package com.kaiqkt.auth.domain.repositories

import com.kaiqkt.auth.domain.models.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface RoleRepository : JpaRepository<Role, String> {
    fun findByName(name: String): Role?

    @Query(
        "SELECT r FROM Role r WHERE :query IS NULL OR " +
                "r.id = :query OR LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%'))"
    )
    fun findAllByQuery(query: String?, pageable: Pageable): Page<Role>

    @Modifying
    @Transactional
    @Query("DELETE FROM user_roles WHERE role_id IN :roleIds", nativeQuery = true)
    fun removeRolesFromAllUsers(roleIds: List<String>)

    @Modifying
    @Transactional
    @Query("UPDATE Role r SET r.description = :description WHERE r.id = :roleId")
    fun updateDescription(roleId: String, description: String?)
}
