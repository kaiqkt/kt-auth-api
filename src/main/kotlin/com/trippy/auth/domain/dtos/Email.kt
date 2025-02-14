package com.trippy.auth.domain.dtos

import com.trippy.auth.domain.models.User
import com.trippy.auth.domain.utils.Constants.EMAIL_RESET_PASSWORD_TEMPLATE
import com.trippy.auth.domain.utils.Constants.EMAIL_VERIFIED_TEMPLATE
import com.trippy.auth.domain.utils.Constants.EMAIL_VERIFY_TEMPLATE

sealed class Email(
    val recipient: String,
    val subject: String,
    val data: Map<String, String>,
    val template: String
) {
    class VerifyEmail(user: User, redirectLink: String) : Email(
        recipient = user.email,
        subject = "Verify your email",
        data = mapOf(
            "name" to user.firstName,
            "link" to redirectLink,
        ),
        template =  EMAIL_VERIFY_TEMPLATE
    )

    class ResetPassword(user: User, redirectLink: String) : Email(
        recipient = user.email,
        subject = "Reset your password",
        data = mapOf(
            "name" to user.firstName,
            "link" to redirectLink,
        ),
        template = EMAIL_RESET_PASSWORD_TEMPLATE
    )
}