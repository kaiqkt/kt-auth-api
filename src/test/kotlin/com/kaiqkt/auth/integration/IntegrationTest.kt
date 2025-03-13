package com.kaiqkt.auth.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.kaiqkt.auth.domain.models.Session
import com.kaiqkt.auth.domain.models.User
import com.kaiqkt.auth.domain.repositories.RoleRepository
import com.kaiqkt.auth.domain.repositories.SessionRepository
import com.kaiqkt.auth.domain.repositories.UserRepository
import com.kaiqkt.auth.domain.repositories.VerificationRepository
import com.kaiqkt.auth.domain.utils.AuthenticationProperties
import com.kaiqkt.auth.domain.utils.TokenUtils
import com.kaiqkt.auth.integration.utils.SpringMailMock
import com.kaiqkt.auth.unit.domain.models.RoleSampler
import com.kaiqkt.auth.unit.domain.models.UserSampler
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ActiveProfiles("profile")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
class IntegrationTest {
    @LocalServerPort
    var port: Int = 0

    @Value("\${spring.mail.password}")
    lateinit var smtpPassword: String

    @Value("\${spring.mail.username}")
    lateinit var smtpUsername: String

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var sessionRepository: SessionRepository

    @Autowired
    lateinit var verificationRepository: VerificationRepository

    @Autowired
    lateinit var authenticationProperties: AuthenticationProperties


    @BeforeAll
    fun before() {
        RestAssured.baseURI = "http://localhost:$port"
        SpringMailMock.start(smtpUsername, smtpPassword)
    }

    @BeforeEach
    fun beforeEach() {
        SpringMailMock.reset()
        verificationRepository.deleteAll()
        sessionRepository.deleteAll()
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @AfterAll
    fun afterAll() {
        SpringMailMock.stop()
    }

    fun mockUser(): User {
        val user = userRepository.save(UserSampler.sample())
        val roleUser = RoleSampler.sample("USER")
        val roleAdmin = RoleSampler.sample("ADMIN")

        roleRepository.saveAll(listOf(roleAdmin, roleUser))
        return userRepository.save(user.copy(roles = mutableListOf(roleUser, roleAdmin)))
    }

    fun mockAuthentication(): Pair<String, String> {
        val user = mockUser()
        val expireAt = LocalDateTime.now().plusDays(authenticationProperties.refreshTokenExpiration.toLong())
        var session = Session(user = user, expireAt = expireAt)
        val refreshToken = TokenUtils.generateHash(session.id, authenticationProperties.refreshTokenSecret)
        session = sessionRepository.save(session.copy(refreshToken = refreshToken))

        val accessToken = TokenUtils.generateJwt(
            mapOf("user_id" to session.user.id, "session_id" to session.id, "roles" to user.roles.map { it.name }),
            authenticationProperties.accessTokenExpiration,
            authenticationProperties.accessTokenSecret
        )

        return Pair(accessToken, refreshToken)
    }
}
