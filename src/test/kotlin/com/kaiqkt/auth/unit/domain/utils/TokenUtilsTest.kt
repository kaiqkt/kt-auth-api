package com.kaiqkt.auth.unit.domain.utils

import com.auth0.jwt.exceptions.SignatureVerificationException
import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.domain.utils.TokenUtils
import io.azam.ulidj.ULID
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TokenUtilsTest {

    @Test
    fun `given a map of claims, expiration time and the secret should generate the token`() {
        val claims = mapOf(
            "string" to "string",
            "int" to 1,
            "long" to 1L,
            "boolean" to true,
            "double" to 1.0,
            "list" to listOf("string", 1)
        )

        val token = TokenUtils.generateJwt(claims, 30L, "secret")
        val decodedJwt = TokenUtils.verifyJwt(token, "secret")

        assertEquals(decodedJwt.token, token)
        assertEquals(decodedJwt.getClaim("string").asString(), "string")
        assertEquals(decodedJwt.getClaim("int").asInt(), 1)
        assertEquals(decodedJwt.getClaim("long").asLong(), 1L)
        assertEquals(decodedJwt.getClaim("boolean").asBoolean(), true)
        assertEquals(decodedJwt.getClaim("double").asDouble(), 1.0)
        assertEquals(decodedJwt.getClaim("list").asList(String::class.java), listOf("string"))
    }

    @Test
    fun `given a map of claims with a invalid type of value should throw a exception`() {
        val claims = mapOf(
            "invalid" to LocalDateTime.now()
        )

        assertThrows<IllegalArgumentException> { TokenUtils.generateJwt(claims, 30L, "secret") }
    }

    @Test
    fun `given a token with a expired time should throw a DomainException`() {
        val claims = mapOf("string" to "string")

        val token = TokenUtils.generateJwt(claims, -30L, "secret")

        val exception = assertThrows<DomainException> { TokenUtils.verifyJwt(token, "secret") }

        assertEquals(exception.type, ErrorType.TOKEN_EXPIRED)
    }

    @Test
    fun `given a token with a invalid signature should throw a exception`() {
        val claims = mapOf("string" to "string")

        val token = TokenUtils.generateJwt(claims, 30L, "invalid-secret")

        assertThrows<SignatureVerificationException> { TokenUtils.verifyJwt(token, "secret") }
    }

    @Test
    fun `given a map of data and a secret should generate a hash`() {
        val id = ULID.random()
        val secret = "secret"

        val hash = TokenUtils.generateHash(id, secret)

        assertTrue { TokenUtils.verifyHash(hash, id, secret) }
    }
}
