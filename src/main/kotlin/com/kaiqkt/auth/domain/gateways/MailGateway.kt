package com.kaiqkt.auth.domain.gateways

import com.kaiqkt.auth.domain.dtos.Email

interface MailGateway {
    fun send(email: Email)
}
