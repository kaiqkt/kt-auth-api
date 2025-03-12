package com.kaiqkt.auth.unit.application.web.request

import com.kaiqkt.auth.generated.application.web.dtos.PasswordRequestV1

object PasswordRequest {
    fun sample() = PasswordRequestV1(
        newPassword = "Password@123",
        oldPassword = "Password@123"
    )
}
