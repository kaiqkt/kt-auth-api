package com.trippy.auth.unit.domain.services

import com.trippy.auth.domain.exceptions.DomainException
import com.trippy.auth.domain.exceptions.ErrorType
import com.trippy.auth.domain.models.Role
import com.trippy.auth.domain.repositories.RoleRepository
import com.trippy.auth.domain.services.RoleService
import com.trippy.auth.unit.domain.models.RoleSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*
import kotlin.test.assertEquals

class RoleServiceTest {

    private val roleRepository: RoleRepository = mockk()
    private val roleService: RoleService = RoleService(roleRepository)

    @Test
    fun `given a name when find role should return role`() {
        val role = RoleSampler.sample()

        every { roleRepository.findByName(any<String>()) } returns role

        val result = roleService.findByName(role.name)

        assertEquals(role, result)
        verify { roleRepository.findByName(role.name) }
    }

    @Test
    fun `given a name when role not found should throw exception`() {
        val name = "admin"

        every { roleRepository.findByName(any<String>()) } returns null

        val exception = assertThrows<DomainException> { roleService.findByName(name) }

        verify { roleRepository.findByName(name) }
        assertEquals(exception.type, ErrorType.ROLE_NOT_FOUND)
    }

    @Test
    fun `given role to create when does not exist should save and return role`() {
        val role = RoleSampler.sample()

        every { roleRepository.save(any<Role>()) } returns role
        every { roleRepository.findByName(any<String>()) } returns null

        val result = roleService.create(role)

        assertEquals(role, result)
        verify { roleRepository.save(role) }
        verify { roleRepository.findByName(role.name) }
    }

    @Test
    fun `given role without prefix should init with the prefix save and return`() {
        val role = RoleSampler.sample()

        every { roleRepository.save(any<Role>()) } returns role
        every { roleRepository.findByName(any<String>()) } returns null

        val result = roleService.create(role)

        assertEquals(role, result)
        assertEquals(role.name, "ROLE_ADMIN")
        verify { roleRepository.save(role) }
        verify { roleRepository.findByName(role.name) }
    }

    @Test
    fun `given role when already exists should return the existing role`() {
        val role = RoleSampler.sample()

        every { roleRepository.findByName(any<String>()) } returns role

        val result = roleService.create(role)

        assertEquals(role, result)
        verify { roleRepository.findByName(role.name) }
        verify(exactly = 0) { roleRepository.save(role) }
    }

    @Test
    fun `given pagination and a query when exist roles should return the roles with the pagination`() {
        val roles = listOf(RoleSampler.sample())
        val pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "name")
        val page = PageImpl(roles, pageRequest, roles.size.toLong())

        every { roleRepository.findAllByQuery(any(), any()) } returns page

        val result = roleService.findAll("admin", pageRequest)

        assertEquals(page, result)
        verify { roleRepository.findAllByQuery("admin", pageRequest) }
    }

    @Test
    fun `given ids when find role should return list of roles`() {
        val ids = listOf("1", "2")
        val roles = listOf(RoleSampler.sample("user"), RoleSampler.sample())

        every { roleRepository.findAllById(any()) } returns roles

        val result = roleService.findAllById(ids)

        assertEquals(roles, result)
        verify { roleRepository.findAllById(ids) }
    }

    @Test
    fun `given ids should delete roles`() {
        val ids = listOf("1", "2")

        justRun { roleRepository.deleteAllById(any()) }
        justRun { roleRepository.removeRolesFromAllUsers(any()) }

        roleService.deleteAllById(ids)

        verify { roleRepository.deleteAllById(ids) }
        verify { roleRepository.removeRolesFromAllUsers(ids) }
    }

    @Test
    fun `given id and description should update role description`() {
        val id = "1"
        val description = "description"
        val role = RoleSampler.sample()

        justRun { roleRepository.updateDescription(any(), any()) }
        every { roleRepository.findById(any()) } returns Optional.of(role)

        roleService.updateDescription(id, description)

        verify { roleRepository.updateDescription(id, description) }
        verify { roleRepository.findById(id) }
    }

    @Test
    fun `given id when role not found should throw exception`() {
        val id = "1"

        justRun { roleRepository.updateDescription(any(), any()) }
        every { roleRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> { roleService.updateDescription(id, "description") }

        verify { roleRepository.findById(id) }
        assertEquals(exception.type, ErrorType.ROLE_NOT_FOUND)
    }
}