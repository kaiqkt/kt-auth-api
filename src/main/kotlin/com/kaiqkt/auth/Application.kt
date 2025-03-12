package com.kaiqkt.auth

import com.kaiqkt.auth.domain.utils.AuthenticationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
@ComponentScan(basePackages = ["com.kaiqkt", "com.kaiqkt"])
@EnableConfigurationProperties(AuthenticationProperties::class)
class Application

fun main(args: Array<String>) {
	runApplication<Application>(args = args)
}
