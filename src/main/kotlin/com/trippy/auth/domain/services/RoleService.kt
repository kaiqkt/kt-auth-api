package com.trippy.auth.domain.services

import com.trippy.auth.domain.exceptions.DomainException
import com.trippy.auth.domain.exceptions.ErrorType
import com.trippy.auth.domain.models.Role
import com.trippy.auth.domain.repositories.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class RoleService(private val roleRepository: RoleRepository) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun create(role: Role): Role = roleRepository.findByName(role.name) ?: roleRepository.save(role).also {
        log.info("Role created: $it")
    }

    fun findAll(query: String?, pageRequest: PageRequest): Page<Role> =
        roleRepository.findAllByQuery(query, pageRequest)

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