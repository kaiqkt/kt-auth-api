package com.kaiqkt.auth.domain.services

import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.domain.models.User
import com.kaiqkt.auth.domain.models.enums.VerificationType
import com.kaiqkt.auth.domain.repositories.CredentialRepository
import com.kaiqkt.auth.domain.repositories.UserRepository
import com.kaiqkt.auth.domain.utils.Constants
import jakarta.transaction.Transactional
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val roleService: RoleService,
    private val userRepository: UserRepository,
    private val credentialRepository: CredentialRepository,
    private val sessionService: SessionService,
    private val verificationService: VerificationService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun create(user: User): User {
        if (userRepository.existsByEmail(user.email)) {
            throw DomainException(ErrorType.EMAIL_ALREADY_EXISTS)
        }

        val role = roleService.findByName(Constants.ROLE_USER)

        return user.copy(roles = listOf(role).toMutableList()).let {
            userRepository.save(it).also {
                log.info("User ${it.id} created successfully")
            }
        }
    }

    @Transactional
    fun verifyEmail(code: String): String {
        return runCatching {
            val verification = verificationService.findByCode(code)
            userRepository.updateIsVerified(verification.user.id)
            log.info("Email verified for user ${verification.user.id}")
            verificationService.delete(verification)

            this::class.java.getResource(Constants.EMAIL_VERIFIED_TEMPLATE)!!.readText()
        }.getOrElse {
            this::class.java.getResource(Constants.EMAIL_VERIFY_FAIL_TEMPLATE)!!.readText()
        }
    }

    fun resetPassword(userId: String, oldPassword: String, newPassword: String) {
        val user = findById(userId)

        if (!BCrypt.checkpw(oldPassword, user.credential.hash)) {
            throw DomainException(ErrorType.INVALID_CREDENTIAL)
        }

        credentialRepository.updateHash(userId, BCrypt.hashpw(newPassword, BCrypt.gensalt()))
        sessionService.revokeAllByUserId(userId)

        log.info("Password updated for user $userId")
    }

    fun resetPasswordRequest(email: String) {
        val user = findByEmail(email)
        CoroutineScope(Dispatchers.IO).launch {
            verificationService.send(user, VerificationType.PASSWORD)
        }
        log.info("Reset password request sent for user ${user.id}")
    }

    fun resetPassword(code: String, password: String) {
        val verification = verificationService.findByCode(code)
        val user = verification.user

        credentialRepository.updateHash(user.id, BCrypt.hashpw(password, BCrypt.gensalt()))
        verificationService.delete(verification)
        sessionService.revokeAllByUserId(user.id)

        log.info("Password for user ${user.id} updated successfully")
    }

    fun addRoles(userId: String, rolesIds: List<String>): User {
        val user = userRepository.findById(userId).getOrNull() ?: throw DomainException(ErrorType.USER_NOT_FOUND)
        val roles = roleService.findAllById(rolesIds)
        val userUpdated = user.copy(roles = user.roles.plus(roles).toSet().toMutableList())

        if (roles.isEmpty()) {
            return user
        }

        return userRepository.save(userUpdated).also {
            log.info("Roles ${rolesIds.joinToString()} added to user $userId")
        }
    }

    fun removeRoles(userId: String, rolesIds: List<String>): User {
        val user = findById(userId)
        val roles = user.roles.filterNot { it.id in rolesIds }.toMutableList()

        return userRepository.save(user.copy(roles = roles)).also {
            log.info("Roles ${rolesIds.joinToString()} removed from user $userId")
        }
    }

    fun findById(id: String): User {
        return userRepository.findById(id).getOrNull() ?: throw DomainException(ErrorType.USER_NOT_FOUND)
    }

    fun findByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw DomainException(ErrorType.USER_NOT_FOUND)
    }

    fun findAll(query: String?, pageRequest: PageRequest) = userRepository.findAllByQuery(query, pageRequest)
}
