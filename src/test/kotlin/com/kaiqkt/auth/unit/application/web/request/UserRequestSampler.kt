package com.kaiqkt.auth.unit.application.web.request

import com.kaiqkt.auth.generated.application.web.dtos.UserRequestV1


object UserRequestSampler {
    fun sample() = UserRequestV1(
        firstName = "John",
        lastName = "Doe",
        email = "test@email.com",
        password = "Password@123"
    )
}
