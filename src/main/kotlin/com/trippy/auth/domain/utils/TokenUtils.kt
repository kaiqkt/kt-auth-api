package com.trippy.auth.domain.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.trippy.auth.domain.exceptions.DomainException
import com.trippy.auth.domain.exceptions.ErrorType
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import org.slf4j.LoggerFactory

object TokenUtils {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun generateJwt(claims: Map<String, Any>, expiration: Long, secret: String): String {
        val jwtBuilder = JWT.create()
            .withIssuer("kaiqkt")
            .withExpiresAt(LocalDateTime.now().plusMinutes(expiration).atZone(ZoneId.systemDefault()).toInstant())

        claims.forEach { (key, value) ->
            when (value) {
                is String -> jwtBuilder.withClaim(key, value)
                is Int -> jwtBuilder.withClaim(key, value)
                is Long -> jwtBuilder.withClaim(key, value)
                is Boolean -> jwtBuilder.withClaim(key, value)
                is Double -> jwtBuilder.withClaim(key, value)
                is List<*> -> jwtBuilder.withArrayClaim(key, value.filterIsInstance<String>().toTypedArray())
                else -> throw IllegalArgumentException("Unsupported claim type for key: $key")
            }
        }

        return jwtBuilder.sign(Algorithm.HMAC256(secret))
    }

    fun generateHash(data: String, secret: String): String {
        val algorithm = Algorithm.HMAC256(secret)
        val hash = algorithm.sign(data.toByteArray())

        return Base64.getEncoder().encodeToString(hash)
    }

    fun verifyHash(token: String, data: String, secret: String): Boolean {
        return token == generateHash(data, secret)
    }

    fun verifyJwt(token: String, secret: String): DecodedJWT {
        val algorithm = Algorithm.HMAC256(secret)
        val verifier = JWT.require(algorithm)
            .build()

        return try {
            verifier.verify(token)
        } catch (e: TokenExpiredException) {
            log.error("Token expired: {}", e.message)
            throw DomainException(ErrorType.TOKEN_EXPIRED)
        }
    }
}