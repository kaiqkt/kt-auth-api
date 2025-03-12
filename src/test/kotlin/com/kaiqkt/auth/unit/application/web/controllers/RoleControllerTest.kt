package com.kaiqkt.auth.unit.application.web.controllers

import com.kaiqkt.auth.application.web.controllers.RoleController
import com.kaiqkt.auth.application.web.responses.toV1
import com.kaiqkt.auth.domain.models.Role
import com.kaiqkt.auth.domain.services.RoleService
import com.kaiqkt.auth.unit.application.web.request.RoleRequestSampler
import com.kaiqkt.auth.unit.domain.models.RoleSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus

class RoleControllerTest {

    private val roleService: RoleService = mockk()
    private val roleController: RoleController = RoleController(roleService)

    @Test
    fun `given a role should create successfully`() {
        val roleRequest = RoleRequestSampler.sample()
        val role = RoleSampler.sample()

        every { roleService.create(any()) } returns role

        val response = roleController.create(roleRequest)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(role.toV1(), response.body)
    }

    @Test
    fun `given a role id and description should update successfully`() {
        val roleId = "1"
        val role = RoleSampler.sample()

        every { roleService.updateDescription(any(), any()) } returns role

        val response = roleController.update(roleId, RoleRequestSampler.sampleUpdate())

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(role.toV1(), response.body)
    }

    @Test
    fun `given roles ids should successfully`() {
        val request = RoleRequestSampler.sampleDelete()

        justRun { roleService.deleteAllById(any()) }

        val response = roleController.delete(request)

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify { roleService.deleteAllById(request.ids) }
    }

    @Test
    fun `given parameter when found roles should return successfully`() {
        val roles = listOf(RoleSampler.sample(), RoleSampler.sample("User"))
        val pageRoles: Page<Role> = PageImpl(roles)

        every { roleService.findAll(any(), any()) } returns pageRoles

        val response = roleController.getAll(0, 10, "ASC", "name", "Admin")

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(2, response.body?.elements?.size)
    }
}
