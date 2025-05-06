package com.kaiqkt.auth.integration.web

import com.kaiqkt.auth.application.web.requests.toDomain
import com.kaiqkt.auth.application.web.responses.toV1
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.generated.application.web.dtos.ErrorV1
import com.kaiqkt.auth.generated.application.web.dtos.IdsRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.PageResponseV1
import com.kaiqkt.auth.generated.application.web.dtos.PasswordRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.ResetPasswordRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.UserResponseV1
import com.kaiqkt.auth.integration.IntegrationTest
import com.kaiqkt.auth.integration.utils.SpringMailMock
import com.kaiqkt.auth.unit.application.web.request.PasswordRequest
import com.kaiqkt.auth.unit.application.web.request.UserRequestSampler
import com.kaiqkt.auth.unit.domain.models.RoleSampler
import com.kaiqkt.auth.unit.domain.models.UserSampler
import com.kaiqkt.auth.unit.domain.models.VerificationSampler
import io.restassured.RestAssured.given
import org.apache.commons.text.StringSubstitutor
import org.springframework.security.crypto.bcrypt.BCrypt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserIntegrationTest : IntegrationTest() {

    @Test
    fun `given a delete request and a user id when the user is not found should return status 404`() {
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .delete("/user/test_user_id")
            .then()
            .statusCode(404)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.USER_NOT_FOUND.name)
    }

    @Test
    fun `given a delete request and a user id when the user is found should return status 204`() {
        val authentication = mockAuthentication()
        val user = userRepository.findAll().first()

        given()
            .header("Authorization", "Bearer ${authentication.first}")
            .delete("/user/${user.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a user request when the first name is empty should return status 400`() {
        val request = UserRequestSampler.sample().copy(firstName = "")
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_ARGUMENTS.name)
        assertEquals(error.errors?.get("firstName"), "first name size must be between 1 to 255 characters")
    }

    @Test
    fun `given a user request when the first name exceed 255 characters should return status 400`() {
        val request = UserRequestSampler.sample().copy(firstName = "a".repeat(256))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_ARGUMENTS.name)
        assertEquals(error.errors?.get("firstName"), "first name size must be between 1 to 255 characters")
    }

    @Test
    fun `given a user request when the last name exceed 255 characters should return status 400`() {
        val request = UserRequestSampler.sample().copy(lastName = "a".repeat(256))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_ARGUMENTS.name)
        assertEquals(error.errors?.get("lastName"), "last name must not exceed 255 characters")
    }

    @Test
    fun `given a user request when the data is valid should create successfully`() {
        val request = UserRequestSampler.sample().copy(email = "test1@email.com")
        val authentication = mockAuthentication()

        given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(mapper.writeValueAsString(request))
            .post("/user")
            .then()
            .statusCode(200)

        assertEquals(2, userRepository.count())
    }

    @Test
    fun `given a user request when the email is in use should return http status 409`() {
        val request = UserRequestSampler.sample()
        val authentication = mockAuthentication()

        userRepository.save(request.toDomain())

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(mapper.writeValueAsString(request))
            .post("/user")
            .then()
            .statusCode(409)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.EMAIL_ALREADY_EXISTS.name)
    }

    @Test
    fun `given a user request when the email is empty should return status 400`() {
        val request = UserRequestSampler.sample().copy(email = "")
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_ARGUMENTS.name)
        assertEquals(error.errors?.get("email"), "email must not be empty")
    }

    @Test
    fun `given a user request when the email is not valid should return status 400`() {
        val request = UserRequestSampler.sample().copy(email = "test_email")
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_ARGUMENTS.name)
        assertEquals(error.errors?.get("email"), "email must be a valid address")
    }

    @Test
    fun `given a user request when the password is not valid should return status 400`() {
        val request = UserRequestSampler.sample().copy(password = "test_password")
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_ARGUMENTS.name)
        assertEquals(
            error.errors?.get("password"), "password must be at least 8 characters long and" +
                    " include at least one letter, one special character, and one number"
        )
    }

    @Test
    fun `given a request to add roles to a user when the user is not found should return status 404`() {
        val request = IdsRequestV1(listOf("test_role"))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user/test_user_id/role")
            .then()
            .statusCode(404)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.USER_NOT_FOUND.name)
    }

    @Test
    fun `given a request to add roles to a user when the roles are not found should return user not updated`() {
        val user = userRepository.save(UserSampler.sample())
        val request = IdsRequestV1(listOf("test_role"))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user/${user.id}/role")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val userResponseV1 = mapper.readValue(response.body.asString(), UserResponseV1::class.java)

        assertEquals(userResponseV1.roles, user.roles.map { it.toV1() })
    }

    @Test
    fun `given a request to add roles to a user when the roles are found should return user updated`() {
        val user = userRepository.save(UserSampler.sample())
        val roles = listOf(RoleSampler.sample(), RoleSampler.sample("User"))
        roleRepository.saveAll(roles)
        val request = IdsRequestV1(listOf(roles[0].id, roles[1].id))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user/${user.id}/role")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val userResponseV1 = mapper.readValue(response.body.asString(), UserResponseV1::class.java)

        assertTrue { userResponseV1.roles.containsAll(user.roles.map { it.toV1() }) }
    }

    @Test
    fun `given a request to add roles when the user already has the role should not duplicate`() {
        val user = userRepository.save(UserSampler.sample())
        val role = roleRepository.save(RoleSampler.sample())
        userRepository.save(user.copy(roles = mutableListOf(role)))
        val authentication = mockAuthentication()

        val request = IdsRequestV1(listOf(role.id))

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .post("/user/${user.id}/role")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val userResponseV1 = mapper.readValue(response.body.asString(), UserResponseV1::class.java)

        assertEquals(1, userResponseV1.roles.size)
        assertTrue { userResponseV1.roles.contains(role.toV1()) }
    }

    @Test
    fun `given a request to find a user when the user is not found should return status 404`() {
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/user/test_user_id")
            .then()
            .statusCode(404)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.USER_NOT_FOUND.name)
    }

    @Test
    fun `given a request to find a user when the user is found should return user`() {
        val user = userRepository.save(UserSampler.sample())
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/user/${user.id}")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val userResponseV1 = mapper.readValue(response.body.asString(), UserResponseV1::class.java)

        assertEquals(userResponseV1, user.toV1())
    }

    @Test
    fun `given a request to remove roles from a user when the user is not found should return status 404`() {
        val request = IdsRequestV1(listOf("test_role"))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .patch("/user/test_user_id/role")
            .then()
            .statusCode(404)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.USER_NOT_FOUND.name)
    }

    @Test
    fun `given a request to remove roles from a user should return user not updated`() {
        val user = userRepository.save(UserSampler.sample())
        val role = roleRepository.save(RoleSampler.sample())
        userRepository.save(user.copy(roles = mutableListOf(role)))
        val request = IdsRequestV1(listOf(role.id))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .patch("/user/${user.id}/role")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val userResponseV1 = mapper.readValue(response.body.asString(), UserResponseV1::class.java)

        assertTrue { userResponseV1.roles.isEmpty() }
    }

    @Test
    fun `given a request to find all users should return a page of users`() {
        userRepository.saveAll(listOf(UserSampler.sample(), UserSampler.sample()))
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/users")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val pageResponse = mapper.readValue(response.body.asString(), PageResponseV1::class.java)

        assertEquals(pageResponse.totalElements, 3)
    }

    @Test
    fun `given a request to find all users when page size exceed the limit should return a error`() {
        userRepository.saveAll(listOf(UserSampler.sample(), UserSampler.sample()))
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/users?size=1000")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals(error.errors?.get("size"), "must be less than or equal to 999")
    }

    @Test
    fun `given a request to find all users when the query is user id should return the user that match with the id`() {
        val users = userRepository.saveAll(listOf(UserSampler.sample(), UserSampler.sample()))
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/users?query=${users.first().id}")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val pageResponse = mapper.readValue(response.body.asString(), PageResponseV1::class.java)

        assertEquals(pageResponse.totalElements, 1)
    }

    @Test
    fun `given a request to find all users when the query is email should return the users that matches`() {
        userRepository.saveAll(
            listOf(
                UserSampler.sample(),
                UserSampler.sample().copy(email = "test@level")
            )
        )
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/users?query=email")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val pageResponse = mapper.readValue(response.body.asString(), PageResponseV1::class.java)

        assertEquals(pageResponse.totalElements, 2)
    }

    @Test
    fun `given a request to find user information when user id is in the access token should return it successfully`() {
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/user")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val userResponseV1 = mapper.readValue(response.body.asString(), UserResponseV1::class.java)
        val user = userRepository.findAll().first()

        assertEquals(userResponseV1, user.toV1())
    }

    @Test
    fun `given a request to update password when the request is valid should update successfully`() {
        val request = PasswordRequest.sample().copy(newPassword = "Password@1234")
        val authentication = mockAuthentication()

        given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(mapper.writeValueAsString(request))
            .patch("/user/password")
            .then()
            .statusCode(200)

        val session = sessionRepository.findAll().first()
        val user = userRepository.findAll().first()

        assertNotNull(session.revokedAt)
        assertTrue { BCrypt.checkpw("Password@1234", user.credential.hash) }
    }

    @Test
    fun `given a request to update password when the new password is invalid should return a error`() {
        val request = PasswordRequest.sample().copy(newPassword = "Password1234")
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(mapper.writeValueAsString(request))
            .patch("/user/password")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_ARGUMENTS.name)
        assertEquals(
            error.errors?.get("newPassword"),
            "password must be at least 8 characters long and include at least one letter, " +
                    "one special character, and one number"
        )
    }

    @Test
    fun `given a request to update password when the old password is invalid should return a error`() {
        val request = PasswordRequest.sample().copy(oldPassword = "Password@12")
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(mapper.writeValueAsString(request))
            .patch("/user/password")
            .then()
            .statusCode(401)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_CREDENTIAL.name)
    }

    @Test
    fun `given a request to verify email when the code is invalid should return a error`() {
        val expected = this::class.java.getResource("/templates/email-verification-failed.html")?.readText()

        val response = given()
            .get("/user/verify-email?code=invalid_code")
            .then()
            .statusCode(200)
            .extract()
            .response()

        assertEquals(response.contentType, "text/html")
        assertEquals(response.body.asString(), expected)

    }

    @Test
    fun `given a request to verify email when the code is valid should return ok`() {
        val expected = this::class.java.getResource("/templates/email-verified.html")?.readText()
        val user = userRepository.save(UserSampler.sample())

        val verification = verificationRepository.save(VerificationSampler.sample().copy(user = user))

        val response = given()
            .get("/user/verify-email?code=${verification.code}")
            .then()
            .statusCode(200)
            .extract()
            .response()

        assertEquals(response.contentType, "text/html")
        assertEquals(response.body.asString(), expected)
    }

    @Test
    fun `given a request to request reset password when the email is invalid should return a error`() {
        val request = UserRequestSampler.sample().copy(email = "invalid_email")

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .body(mapper.writeValueAsString(request))
            .post("/user/reset-password")
            .then()
            .statusCode(404)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.USER_NOT_FOUND.name)
    }

    @Test
    fun `given a request to request reset password when the email is valid should return ok`() {
        val user = userRepository.save(UserSampler.sample())

        val request = UserRequestSampler.sample().copy(email = user.email)

        given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .body(mapper.writeValueAsString(request))
            .post("/user/reset-password")
            .then()
            .statusCode(204)

        assertTrue { SpringMailMock.verify() }
    }

    @Test
    fun `given a request to confirm reset password when the code is invalid should return a error`() {
        val request = ResetPasswordRequestV1("Password@1234")

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .body(mapper.writeValueAsString(request))
            .post("/user/invalid_code/reset-password")
            .then()
            .statusCode(404)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(error.type, ErrorType.INVALID_VERIFICATION_CODE.name)
    }

    @Test
    fun `given a request to confirm reset password when the code is valid should return ok and update the password`() {
        val user = userRepository.save(UserSampler.sample())

        val verification = verificationRepository.save(VerificationSampler.sample().copy(user = user))

        val request = ResetPasswordRequestV1("Password@1236")

        given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .body(mapper.writeValueAsString(request))
            .post("/user/${verification.code}/reset-password")
            .then()
            .statusCode(204)


        assertEquals(0, sessionRepository.count())
        assertTrue { BCrypt.checkpw("Password@1236", userRepository.findAll().first().credential.hash) }
    }

    @Test
    fun `given a valid password request when updatePassword is called should update the password successfully`() {
        val request = PasswordRequestV1(
            oldPassword = "Password@123",
            newPassword = "NewPassword@123"
        )

        given()
            .contentType("application/vnd.kaiqkt_auth_user_v1+json")
            .header("Authorization", "Bearer ${mockAuthentication().first}")
            .body(request)
            .patch("/user/password")
            .then()
            .statusCode(200)

        val updatedUser = userRepository.findAll().first()
        assertTrue { BCrypt.checkpw("NewPassword@123", updatedUser.credential.hash) }
    }

    @Test
    fun `given code should open the form to update the password`() {
        val expected = this::class.java.getResource("/static/reset-password-form.html")?.readText()

        val response = given()
            .get("/user/123/reset-password-form")
            .then()
            .statusCode(200)
            .extract()
            .response()

        assertEquals(response.contentType, "text/html")
        assertEquals(response.body.asString(), StringSubstitutor(mapOf("code" to 123)).replace(expected))
    }
}
