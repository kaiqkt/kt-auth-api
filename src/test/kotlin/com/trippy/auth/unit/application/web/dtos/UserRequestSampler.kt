package com.trippy.auth.unit.application.web.dtos

import com.trippy.auth.generated.application.web.dtos.UserRequestV1


object UserRequestSampler {
    fun sample() = UserRequestV1(
        firstName = "John",
        lastName = "Doe",
        email = "test@email.com",
        password = "Password@123"
    )
}