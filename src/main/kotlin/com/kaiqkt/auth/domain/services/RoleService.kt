package com.kaiqkt.auth.domain.services

import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.domain.models.Role
import com.kaiqkt.auth.domain.repositories.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.sort
import kotlin.jvm.optionals.getOrNull

@Service
class RoleService(private val roleRepository: RoleRepository) {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        val ALLOWED_SORT_COLUMNS = setOf("name", "createdAt", "updatedAt")
    }

    fun create(role: Role): Role = roleRepository.findByName(role.name) ?: roleRepository.save(role).also {
        log.info("Role created: $it")
    }

    fun findAll(query: String?, pageRequest: PageRequest): Page<Role> {
        val invalidProperty = pageRequest.sort
            .map { it.property }
            .firstOrNull { it !in ALLOWED_SORT_COLUMNS }

        if (invalidProperty != null) {
            throw DomainException(ErrorType.INVALID_SORT_PROPERTY)
        }

        return roleRepository.findAllByQuery(query, pageRequest)
    }

    fun findByName(name: String): Role = roleRepository.findByName(name)
        ?: throw DomainException(ErrorType.ROLE_NOT_FOUND)

    fun findAllById(ids: List<String>): List<Role> = roleRepository.findAllById(ids)

    @Transactional
    fun deleteAllById(ids: List<String>) {
        roleRepository.removeRolesFromAllUsers(ids)
        roleRepository.deleteAllById(ids).also {
            log.info("Roles deleted: $ids")
        }
    }

    @Transactional
    fun updateDescription(id: String, description: String?): Role {
        roleRepository.updateDescription(id, description)
        return roleRepository.findById(id).getOrNull() ?: throw DomainException(ErrorType.ROLE_NOT_FOUND)
    }
}
