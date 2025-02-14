package com.trippy.auth.domain.utils

object Constants {
    const val ISSUER = "kaiqkt"
    const val USER_ID = "user_id"
    const val SESSION_ID = "session_id"
    const val ROLES = "roles"
    const val ROLE_ADMIN = "ROLE_ADMIN"
    const val ROLE_USER = "ROLE_USER"
    const val EMAIL_VERIFICATION_SPAM_DELAY = 5L
    const val EMAIL_VERIFIED_TEMPLATE =  "/templates/email-verified.html"
    const val EMAIL_VERIFY_TEMPLATE =  "/templates/verify-email.html"
    const val EMAIL_RESET_PASSWORD_TEMPLATE =  "/templates/reset-password.html"
    const val EMAIL_VERIFY_FAIL_TEMPLATE =  "/templates/email-verification-failed.html"
}