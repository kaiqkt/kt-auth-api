package com.trippy.auth.unit.domain.models

import com.trippy.auth.domain.models.Credential
import com.trippy.auth.domain.models.User
import org.springframework.security.crypto.bcrypt.BCrypt

object UserSampler {
    private val password = BCrypt.hashpw("Password@123", BCrypt.gensalt())

    fun sample(): User {
        val user = User(
            firstName = "John",
            lastName = "Doe",
            email = "test@email.com",
            isVerified = true
        )

        val credential = Credential(
            hash = password,
            user = user
        )

        return user.copy(credential = credential)
    }
}
