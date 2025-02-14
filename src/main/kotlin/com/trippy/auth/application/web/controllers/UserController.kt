package com.trippy.auth.application.web.controllers

import com.kaiqkt.springtools.security.utils.Context
import com.trippy.auth.application.web.requests.toDomain
import com.trippy.auth.application.web.responses.toV1
import com.trippy.auth.domain.models.User
import com.trippy.auth.domain.services.UserService
import com.trippy.auth.domain.utils.Constants
import com.trippy.auth.generated.application.web.controllers.UserApi
import com.trippy.auth.generated.application.web.controllers.UsersApi
import com.trippy.auth.generated.application.web.dtos.IdsRequestV1
import com.trippy.auth.generated.application.web.dtos.PageResponseV1
import com.trippy.auth.generated.application.web.dtos.PasswordRequestV1
import com.trippy.auth.generated.application.web.dtos.ResetPasswordRequestV1
import com.trippy.auth.generated.application.web.dtos.SendResetPasswordRequestV1
import com.trippy.auth.generated.application.web.dtos.UserRequestV1
import com.trippy.auth.generated.application.web.dtos.UserResponseV1
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userService: UserService) : UserApi, UsersApi {

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_API')")
    override fun create(userRequestV1: UserRequestV1): ResponseEntity<UserResponseV1> {
        val user = userRequestV1.toDomain()
        val response = userService.create(user)

        return ResponseEntity.ok(response.toV1())
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    override fun findCurrent(): ResponseEntity<UserResponseV1> {
        val userId = Context.getValue(Constants.USER_ID, String::class.java)
        val response = userService.findById(userId)

        return ResponseEntity.ok(response.toV1())
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_API')")
    override fun addRoles(
        userId: String,
        idsRequestV1: IdsRequestV1
    ): ResponseEntity<UserResponseV1> {
        val response = userService.addRoles(userId, idsRequestV1.ids)

        return ResponseEntity.ok(response.toV1())
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_API')")
    override fun removeRoles(
        userId: String,
        idsRequestV1: IdsRequestV1
    ): ResponseEntity<UserResponseV1> {
        val response = userService.removeRoles(userId, idsRequestV1.ids)

        return ResponseEntity.ok(response.toV1())
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_API')")
    override fun findById(userId: String): ResponseEntity<UserResponseV1> {
        val response = userService.findById(userId)

        return ResponseEntity.ok(response.toV1())
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun getAll(
        page: Int,
        size: Int,
        sort: String,
        orderBy: String,
        query: String?
    ): ResponseEntity<PageResponseV1> {
        val pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(sort), orderBy))
        val response = userService.findAll(query, pageRequest)

        return ResponseEntity.ok(response.toV1(User::toV1))
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    override fun updatePassword(passwordRequestV1: PasswordRequestV1): ResponseEntity<UserResponseV1> {
        val userId = Context.getValue(Constants.USER_ID, String::class.java)
        userService.resetPassword(userId, passwordRequestV1.oldPassword, passwordRequestV1.newPassword)
        return ResponseEntity.ok().build()
    }

    override fun resetPasswordRequest(sendResetPasswordRequestV1: SendResetPasswordRequestV1): ResponseEntity<Unit> {
        userService.resetPasswordRequest(sendResetPasswordRequestV1.email)

        return ResponseEntity.ok().build()
    }

    override fun resetPassword(
        code: String,
        resetPasswordRequestV1: ResetPasswordRequestV1
    ): ResponseEntity<Unit> {
        userService.resetPassword(code, resetPasswordRequestV1.newPassword)

        return ResponseEntity.ok().build()
    }

    override fun verifyEmail(code: String): ResponseEntity<String> {
        val htmlContent = userService.verifyEmail(code)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.TEXT_HTML)
            .body(htmlContent)
    }

}