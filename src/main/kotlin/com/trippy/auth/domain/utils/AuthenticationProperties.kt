package com.trippy.auth.domain.utils

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "springtools")
data class AuthenticationProperties(
    var jwtSecret: String = "",
    var refreshTokenSecret: String = "",
    var refreshTokenExpiration: Long = 0,
    var jwtTokenExpiration: Long = 0
)