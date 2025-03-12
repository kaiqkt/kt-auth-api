package com.kaiqkt.auth.application.web.requests

import com.kaiqkt.auth.domain.models.Credential
import com.kaiqkt.auth.domain.models.User
import com.kaiqkt.auth.generated.application.web.dtos.UserRequestV1
import org.springframework.security.crypto.bcrypt.BCrypt

fun UserRequestV1.toDomain(): User {
    val user = User(
        firstName = this.firstName,
        lastName = this.lastName,
        email = email
    )

    val hash = BCrypt.hashpw(this.password, BCrypt.gensalt())

    val credential = Credential(
        hash = hash,
        user = user
    )

    return user.copy(credential = credential)
}
