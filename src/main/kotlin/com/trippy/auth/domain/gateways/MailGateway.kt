package com.trippy.auth.domain.gateways

import com.trippy.auth.domain.dtos.Email

interface MailGateway {
    fun send(email: Email)
}