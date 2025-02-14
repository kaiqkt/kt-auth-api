package com.trippy.auth.unit.application.web.controllers

import com.trippy.auth.application.web.controllers.UserController
import com.trippy.auth.application.web.requests.toDomain
import com.trippy.auth.domain.services.UserService
import com.trippy.auth.generated.application.web.dtos.ResetPasswordRequestV1
import com.trippy.auth.generated.application.web.dtos.IdsRequestV1
import com.trippy.auth.generated.application.web.dtos.SendResetPasswordRequestV1
import com.trippy.auth.unit.application.web.dtos.PasswordRequest
import com.trippy.auth.unit.application.web.dtos.UserRequestSampler
import com.trippy.auth.unit.application.web.security.ContextSampler
import com.trippy.auth.unit.domain.models.UserSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserControllerTest {
    private val userService: UserService = mockk()
    private val userController: UserController = UserController(userService)

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.getContext().authentication = ContextSampler.sample()
    }

    @Test
    fun `given a user request when create is called should return a user response`() {
        val request = UserRequestSampler.sample()

        every { userService.create(any()) } returns request.toDomain()

        val response = userController.create(request)

        verify { userService.create(any()) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a request to add roles to a user when add roles is called should return the user update`() {
        val request = IdsRequestV1(listOf(ULID.random()))
        val userId = ULID.random()

        every { userService.addRoles(any(), any()) } returns UserSampler.sample()

        val response = userController.addRoles(userId, request)

        verify { userService.addRoles(userId, request.ids) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a request to remove roles from a user when remove roles is called should return the user update`() {
        val request = IdsRequestV1(listOf(ULID.random()))
        val userId = ULID.random()

        every { userService.removeRoles(any(), any()) } returns UserSampler.sample()

        val response = userController.removeRoles(userId, request)

        verify { userService.removeRoles(userId, request.ids) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a user id when findById is called should return the user`() {
        val userId = ULID.random()

        every { userService.findById(any()) } returns UserSampler.sample()

        val response = userController.findById(userId)

        verify { userService.findById(userId) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a request to find all users when findAll is called should return a list of users`() {
        val users = listOf(UserSampler.sample())
        val page = PageImpl(users)

        every { userService.findAll(any(), any()) } returns page

        val response = userController.getAll(0, 10, "ASC", "createdBy", "test@email.com")

        verify { userService.findAll(any(), any()) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a user id when find is called should return the user`() {

        every { userService.findById(any()) } returns UserSampler.sample()

        val response = userController.findCurrent()

        verify { userService.findById(any()) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a user id and a update password request should update successfully`() {
        val request = PasswordRequest.sample()

        justRun { userService.resetPassword(any(), any(), any()) }

        val response = userController.updatePassword(request)

        verify { userService.resetPassword(any(), any(), any()) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a verify email request when the code is valid should return ok`() {
        val code = "code"

        every { userService.verifyEmail(any()) } returns "<html>success</html>"

        val response = userController.verifyEmail(code)

        verify { userService.verifyEmail(code) }

        assertEquals(response.statusCode, HttpStatus.OK)
        assertNotNull(response.body)
    }

    @Test
    fun `given a verify email request when the code is invalid should return not found`() {
        val code = "code"

        every { userService.verifyEmail(any()) } returns "<html>fail</html>"

        val response = userController.verifyEmail(code)

        verify { userService.verifyEmail(code) }

        assertEquals(response.statusCode, HttpStatus.OK)
        assertNotNull(response.body)
    }

    @Test
    fun `given a request to forgot password should send successfully`() {
        val request = SendResetPasswordRequestV1("test@email.com")

        justRun { userService.resetPasswordRequest(any()) }

        val response = userController.resetPasswordRequest(request)

        verify { userService.resetPasswordRequest(request.email) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given a request to reset password should update successfully`() {
        val request = ResetPasswordRequestV1("password")
        val code = "code"

        justRun { userService.resetPassword(any(), any()) }

        val response = userController.resetPassword(code, request)

        verify { userService.resetPassword(code, request.newPassword) }

        assertEquals(response.statusCode, HttpStatus.OK)
    }
}