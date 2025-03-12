package com.kaiqkt.auth.application.web.controllers

import com.kaiqkt.auth.application.web.requests.toDomain
import com.kaiqkt.auth.application.web.responses.toV1
import com.kaiqkt.auth.domain.models.Role
import com.kaiqkt.auth.domain.services.RoleService
import com.kaiqkt.auth.generated.application.web.controllers.RoleApi
import com.kaiqkt.auth.generated.application.web.controllers.RolesApi
import com.kaiqkt.auth.generated.application.web.dtos.DeleteRolesRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.PageResponseV1
import com.kaiqkt.auth.generated.application.web.dtos.RoleRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.RoleResponseV1
import com.kaiqkt.auth.generated.application.web.dtos.UpdateRoleRequestV1
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class RoleController(private val roleService: RoleService): RoleApi, RolesApi {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun create(roleRequestV1: RoleRequestV1): ResponseEntity<RoleResponseV1> {
        val response = roleService.create(roleRequestV1.toDomain())

        return ResponseEntity.ok(response.toV1())
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun update(
        id: String,
        updateRoleRequestV1: UpdateRoleRequestV1
    ): ResponseEntity<RoleResponseV1> {
        val response = roleService.updateDescription(id, updateRoleRequestV1.description)

        return ResponseEntity.ok(response.toV1())
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun delete(deleteRolesRequestV1: DeleteRolesRequestV1): ResponseEntity<Unit> {
        roleService.deleteAllById(deleteRolesRequestV1.ids)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun getAll(
        page: Int,
        size: Int,
        sort: String,
        orderBy: String,
        query: String?
    ): ResponseEntity<PageResponseV1> {
        val request = PageRequest.of(page, size, Sort.Direction.fromString(sort), orderBy)
        val response = roleService.findAll(query, request)

        return ResponseEntity.ok(response.toV1(Role::toV1))
    }
}
