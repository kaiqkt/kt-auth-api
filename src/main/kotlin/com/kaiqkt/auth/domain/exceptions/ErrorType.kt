package com.kaiqkt.auth.domain.exceptions

import org.springframework.http.HttpStatus

enum class ErrorType(val message: String, val code: Int) {
    INVALID_SORT_PROPERTY("Invalid sort property", HttpStatus.BAD_REQUEST.value()),
    INVALID_ARGUMENTS("Invalid arguments", HttpStatus.BAD_REQUEST.value()),
    EMAIL_ALREADY_EXISTS("Email already exists", HttpStatus.CONFLICT.value()),
    ROLE_NOT_FOUND("Role not found", HttpStatus.NOT_FOUND.value()),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND.value()),
    INVALID_SESSION("Invalid session", HttpStatus.UNAUTHORIZED.value()),
    INVALID_CREDENTIAL("Invalid credential", HttpStatus.UNAUTHORIZED.value()),
    INVALID_TOKEN("Invalid Token", HttpStatus.UNAUTHORIZED.value()),
    INVALID_VERIFICATION_CODE("Invalid verification code", HttpStatus.NOT_FOUND.value()),
    EMAIL_NOT_VERIFIED("Email not verified", HttpStatus.UNAUTHORIZED.value()),
}
