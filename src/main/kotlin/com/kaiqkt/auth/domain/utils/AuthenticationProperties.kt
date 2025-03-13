package com.kaiqkt.auth.domain.utils

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "auth")
data class AuthenticationProperties(
    var accessTokenSecret: String = "",
    var refreshTokenSecret: String = "",
    var refreshTokenExpiration: Long = 0,
    var accessTokenExpiration: Long = 0
)
