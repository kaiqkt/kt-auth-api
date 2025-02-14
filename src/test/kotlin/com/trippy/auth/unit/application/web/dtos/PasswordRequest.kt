package com.trippy.auth.unit.application.web.dtos

import com.trippy.auth.generated.application.web.dtos.PasswordRequestV1

object PasswordRequest {
    fun sample() = PasswordRequestV1(
        newPassword = "Password@123",
        oldPassword = "Password@123"
    )
}