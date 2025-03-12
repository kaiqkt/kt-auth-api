package com.kaiqkt.auth.application.web.responses

import com.kaiqkt.auth.domain.models.Session
import com.kaiqkt.auth.generated.application.web.dtos.SessionResponseV1


fun Session.toV1() = SessionResponseV1(
    id = this.id,
    ip = this.ip,
    userId = this.user.id,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString(),
    expireAt = this.expireAt.toString(),
    revokedAt = this.revokedAt.toString()
)
