package com.kaiqkt.auth.integration.web

import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.generated.application.web.dtos.ErrorV1
import com.kaiqkt.auth.generated.application.web.dtos.IdsRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.PageResponseV1
import com.kaiqkt.auth.generated.application.web.dtos.RoleResponseV1
import com.kaiqkt.auth.integration.IntegrationTest
import com.kaiqkt.auth.unit.application.web.request.RoleRequestSampler
import com.kaiqkt.auth.unit.domain.models.RoleSampler
import com.kaiqkt.auth.unit.domain.models.UserSampler
import io.restassured.RestAssured.given
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class RoleIntegrationTest : IntegrationTest() {

    @Test
    fun `given a role request when is valid should create a role`() {
        val request = RoleRequestSampler.sample()
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .body(request)
            .header("Authorization", "Bearer ${authentication.first}")
            .post("/role")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val roleResponse = mapper.readValue(response.body.asString(), RoleResponseV1::class.java)

        assertEquals("ROLE_ADMIN", roleResponse.name)
    }

    @Test
    fun `given a role request when the name is empty should return http status 400`() {
        val request = RoleRequestSampler.sample().copy(name = "")
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .body(request)
            .header("Authorization", "Bearer ${authentication.first}")
            .post("/role")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals(error.errors?.get("name"), "name size must be between 1 to 50 characters")
    }

    @Test
    fun `given a role request when the description exceed 255 characters should return http status 400`() {
        val request = RoleRequestSampler.sample().copy(name = "a".repeat(255))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .body(request)
            .header("Authorization", "Bearer ${authentication.first}")
            .post("/role")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals(error.errors?.get("name"), "name size must be between 1 to 50 characters")
    }

    @Test
    fun `given a role request when is the name exceed 50 characters should return http status 400`() {
        val request = RoleRequestSampler.sample().copy(name = "a".repeat(51))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .body(request)
            .header("Authorization", "Bearer ${authentication.first}")
            .post("/role")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals(error.errors?.get("name"), "name size must be between 1 to 50 characters")
    }

    @Test
    fun `given a request to update a role description should update the role description`() {
        val role = roleRepository.save(RoleSampler.sample())
        val request = RoleRequestSampler.sampleUpdate()
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .patch("/role/${role.id}")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val roleResponse = mapper.readValue(response.body.asString(), RoleResponseV1::class.java)

        assertEquals("new description", roleResponse.description)
    }


    @Test
    fun `given a request to update a role description null value should update the role description`() {
        val role = roleRepository.save(RoleSampler.sample())
        val request = RoleRequestSampler.sampleUpdate().copy("")
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .patch("/role/${role.id}")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals(error.errors?.get("description"), "description size must be between 1 to 255 characters")
    }

    @Test
    fun `given a request to update a role description that exceed 255 chars should update the role description`() {
        val role = roleRepository.save(RoleSampler.sample())
        val request = RoleRequestSampler.sampleUpdate().copy("a".repeat(256))
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .patch("/role/${role.id}")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals(error.errors?.get("description"), "description size must be between 1 to 255 characters")
    }


    @Test
    fun `given a request to update a role description when the role is not found should return http 404`() {
        val request = RoleRequestSampler.sampleUpdate()
        val authentication = mockAuthentication()

        val response = given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .patch("/role/test_role")
            .then()
            .statusCode(404)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.ROLE_NOT_FOUND.name, error.type)
    }

    @Test
    fun `given delete role request by ids should delete just the given ids and detach the users`() {
        val user = userRepository.save(UserSampler.sample())
        val role = roleRepository.save(RoleSampler.sample())
        userRepository.save(user.copy(roles = mutableListOf(role)))
        val request = IdsRequestV1(ids = listOf(role.id))
        val authentication = mockAuthentication()

        given()
            .contentType("application/vnd.kaiqkt_auth_role_v1+json")
            .header("Authorization", "Bearer ${authentication.first}")
            .body(request)
            .delete("/roles")
            .then()
            .statusCode(204)

        val userUpdated = userRepository.findById(user.id).get()

        assertFalse(userUpdated.roles.contains(role))
        assertEquals(2, roleRepository.count())
    }

    @Test
    fun `given a request to find all roles when exceed the page size should return a error`() {
        val authentication = mockAuthentication()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/roles?size=1000")
            .then()
            .statusCode(400)
            .extract()
            .response()

        val error = mapper.readValue(response.body.asString(), ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_ARGUMENTS.name, error.type)
        assertEquals(error.errors?.get("size"), "must be less than or equal to 999")
    }

    @Test
    fun `given a request to find all roles without query, should return all ordered by name and with sort ASC`() {
        val authentication = mockAuthentication()
        roleRepository.save(RoleSampler.sample().copy(name = "Guest"))
        val roles = roleRepository.findAll()

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/roles")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val pageResponse = mapper.readValue(response.body.asString(), PageResponseV1::class.java)

        assertEquals(3, pageResponse.totalElements)
        assertEquals("ROLE_GUEST", roles.elementAt(2).name)
        assertEquals("ROLE_ADMIN", roles.elementAt(0).name)
        assertEquals("ROLE_USER", roles.elementAt(1).name)
    }

    @Test
    fun `given a request to find all roles that contains USER in the name, should return all that contain the query`() {
        val roles = listOf(
            RoleSampler.sample(),
            RoleSampler.sample().copy(name = "Guest"),
        )
        val authentication = mockAuthentication()
        roleRepository.saveAll(roles)

        val response = given()
            .header("Authorization", "Bearer ${authentication.first}")
            .get("/roles?query=USER")
            .then()
            .statusCode(200)
            .extract()
            .response()

        val pageResponse = mapper.readValue(response.body.asString(), PageResponseV1::class.java)

        assertEquals(1, pageResponse.totalElements)
        assertEquals(4, roleRepository.count())
    }
}
