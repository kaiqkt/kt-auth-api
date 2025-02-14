package com.trippy.auth.unit.domain.services

import com.trippy.auth.domain.exceptions.DomainException
import com.trippy.auth.domain.exceptions.ErrorType
import com.trippy.auth.domain.models.enums.VerificationType
import com.trippy.auth.domain.repositories.CredentialRepository
import com.trippy.auth.domain.repositories.UserRepository
import com.trippy.auth.domain.services.RoleService
import com.trippy.auth.domain.services.SessionService
import com.trippy.auth.domain.services.UserService
import com.trippy.auth.domain.services.VerificationService
import com.trippy.auth.domain.utils.Constants
import com.trippy.auth.unit.domain.models.RoleSampler
import com.trippy.auth.unit.domain.models.UserSampler
import com.trippy.auth.unit.domain.models.VerificationSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTest {
    private val roleService: RoleService = mockk()
    private val userRepository: UserRepository = mockk()
    private val credentialRepository: CredentialRepository = mockk()
    private val sessionService: SessionService = mockk()
    private val verificationService: VerificationService = mockk()
    private val userService =
        UserService(roleService, userRepository, credentialRepository, sessionService, verificationService)

    @Test
    fun `given a new user when the email is not in use should create successfully`() {
        val user = UserSampler.sample()

        every { userRepository.existsByEmail(any()) } returns false
        every { userRepository.save(any()) } returns user
        every { roleService.findByName(any()) } returns RoleSampler.sample()

        userService.create(user)

        verify { userRepository.existsByEmail(user.email) }
        verify { userRepository.save(any()) }
        verify { roleService.findByName(Constants.ROLE_USER) }
    }

    @Test
    fun `given a new user when default role not exist should throw a DomainException`() {
        val user = UserSampler.sample()

        every { userRepository.existsByEmail(any()) } returns false
        every { userRepository.save(any()) } returns user
        every { roleService.findByName(any()) } throws DomainException(ErrorType.ROLE_NOT_FOUND)

        val exception = assertThrows<DomainException> { userService.create(user) }

        assertEquals(exception.type, ErrorType.ROLE_NOT_FOUND)
    }


    @Test
    fun `given a new user when the email is in use should throw a DomainException`() {
        val user = UserSampler.sample()

        every { userRepository.existsByEmail(any()) } returns true
        every { userRepository.save(any()) } returns user

        val exception = assertThrows<DomainException> { userService.create(user) }

        assertEquals(exception.type, ErrorType.EMAIL_ALREADY_EXISTS)
    }

    @Test
    fun `given a user id and roles ids when user not found when adding roles should throw DomainException`() {
        every { userRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> { userService.addRoles("1", listOf("1", "2")) }

        assertEquals(exception.type, ErrorType.USER_NOT_FOUND)

        verify { userRepository.findById("1") }
    }

    @Test
    fun `given a user id and a list of role ids when roles are not found should return user without update`() {
        every { userRepository.findById(any()) } returns Optional.of(UserSampler.sample())
        every { roleService.findAllById(any()) } returns emptyList()

        userService.addRoles("1", listOf("1", "2"))

        verify { userRepository.findById("1") }
        verify { roleService.findAllById(listOf("1", "2")) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `given a user id and a list of role ids when roles are found should return user with roles updated`() {
        val user = UserSampler.sample()
        val roles = mutableListOf(RoleSampler.sample(), RoleSampler.sample())
        val updatedUser = user.copy(roles = user.roles.plus(roles).toMutableList())

        every { userRepository.findById(any()) } returns Optional.of(user)
        every { roleService.findAllById(any()) } returns roles
        every { userRepository.save(any()) } returns updatedUser

        userService.addRoles("1", listOf("1", "2"))

        verify { userRepository.findById("1") }
        verify { roleService.findAllById(listOf("1", "2")) }
        verify { userRepository.save(updatedUser) }
    }

    @Test
    fun `given a user id when the user is not found should throw a DomainException`() {
        every { userRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> { userService.findById("1") }

        assertEquals(exception.type, ErrorType.USER_NOT_FOUND)

        verify { userRepository.findById("1") }
    }

    @Test
    fun `given a user id when the user is found should return the user`() {
        val user = UserSampler.sample()

        every { userRepository.findById(any()) } returns Optional.of(user)

        val result = userService.findById("1")

        assertEquals(result, user)

        verify { userRepository.findById("1") }
    }

    @Test
    fun `given a user id and a list of role ids when the user is found should remove the roles`() {
        val user = UserSampler.sample().copy(roles = mutableListOf(RoleSampler.sample()))

        every { userRepository.findById(any()) } returns Optional.of(user)
        every { userRepository.save(any()) } returns user.copy(roles = mutableListOf())

        userService.removeRoles("1", listOf("1", "2"))

        verify { userRepository.findById("1") }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `given a user id and a list of role ids when the user is NOT found should throw a DomainException`() {
        every { userRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> { userService.removeRoles("1", listOf("1", "2")) }

        verify { userRepository.findById("1") }

        assertEquals(exception.type, ErrorType.USER_NOT_FOUND)
    }

    @Test
    fun `given a pagination should return all user by it`() {
        val pageRequest = PageRequest.of(0, 10, org.springframework.data.domain.Sort.Direction.ASC, "name")

        every { userRepository.findAllByQuery(null, any<PageRequest>()) } returns PageImpl(listOf(UserSampler.sample()))

        userService.findAll(null, pageRequest)

        verify { userRepository.findAllByQuery(null, any<PageRequest>()) }
    }

    @Test
    fun `given an email when the user is not found should throw a DomainException`() {
        every { userRepository.findByEmail(any()) } returns null

        val exception = assertThrows<DomainException> { userService.findByEmail("email") }

        assertEquals(exception.type, ErrorType.USER_NOT_FOUND)

        verify { userRepository.findByEmail("email") }
    }

    @Test
    fun `given an email when the user is found should return the user`() {
        val user = UserSampler.sample()

        every { userRepository.findByEmail(any()) } returns user

        val result = userService.findByEmail("email")

        assertEquals(result, user)

        verify { userRepository.findByEmail("email") }
    }

    @Test
    fun `given a update password request when the old password match should update successfully`() {
        val user = UserSampler.sample()

        every { userRepository.findById(any()) } returns Optional.of(user)
        justRun { credentialRepository.updateHash(any(), any()) }
        justRun { sessionService.revokeAllByUserId(any()) }

        userService.resetPassword("1", "Password@123", "new")

        verify { userRepository.findById("1") }
        verify { credentialRepository.updateHash("1", any()) }
        verify { sessionService.revokeAllByUserId("1") }
    }

    @Test
    fun `given a update password request when the old password dont match should throw a DomainException`() {
        val user = UserSampler.sample()

        every { userRepository.findById(any()) } returns Optional.of(user)

        val exception = assertThrows<DomainException> { userService.resetPassword("1", "old", "new") }

        assertEquals(exception.type, ErrorType.INVALID_CREDENTIAL)

        verify { userRepository.findById("1") }
    }

    @Test
    fun `given a update password request when user dont exists should throw a DomainException`() {

        every { userRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> { userService.resetPassword("1", "Password@123", "new") }

        verify { userRepository.findById("1") }

        assertEquals(exception.type, ErrorType.USER_NOT_FOUND)
    }

    @Test
    fun `given a verification code when find the user should verify him and delete the code`() {
        val verification = VerificationSampler.sample()

        every { verificationService.findByCode(any()) } returns verification
        justRun { verificationService.delete(any()) }
        justRun { userRepository.updateIsVerified(any()) }

        userService.verifyEmail("code")

        verify { verificationService.findByCode("code") }
        verify { userRepository.updateIsVerified(any()) }
        verify { verificationService.delete(verification) }
    }

    @Test
    fun `given a request to reset password when find the user should send successfully`() {
        val user = UserSampler.sample()

        every { userRepository.findByEmail(any()) } returns user
        justRun { runBlocking { verificationService.send(any(), any()) } }

        userService.resetPasswordRequest("email")

        verify { userRepository.findByEmail("email") }
        verify { runBlocking { verificationService.send(user, VerificationType.PASSWORD) } }
    }

    @Test
    fun `given a request to reset password when not find the user should throw a exception`() {
        val user = UserSampler.sample()

        every { userRepository.findByEmail(any()) } returns null
        justRun { runBlocking { verificationService.send(any(), any()) } }

        val exception = assertThrows<DomainException> { userService.resetPasswordRequest("email") }

        verify { userRepository.findByEmail("email") }
        verify(exactly = 0) { runBlocking { verificationService.send(user, VerificationType.PASSWORD) } }

        assertEquals(exception.type, ErrorType.USER_NOT_FOUND)
    }

    @Test
    fun `given a code and a new password when find the user should update the password and delete the code`() {
        val verification = VerificationSampler.sample()

        every { verificationService.findByCode(any()) } returns verification
        justRun { verificationService.delete(any()) }
        justRun { credentialRepository.updateHash(any(), any()) }
        justRun { sessionService.revokeAllByUserId(any()) }

        userService.resetPassword("code", "new")

        verify { verificationService.findByCode("code") }
        verify { credentialRepository.updateHash(verification.user.id, any()) }
        verify { sessionService.revokeAllByUserId(verification.user.id) }
        verify { verificationService.delete(verification) }
    }
}