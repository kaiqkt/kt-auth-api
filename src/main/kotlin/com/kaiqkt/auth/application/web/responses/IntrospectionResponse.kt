package com.kaiqkt.auth.application.web.responses

import com.kaiqkt.auth.domain.dtos.Introspect
import com.kaiqkt.auth.generated.application.web.dtos.IntrospectResponseV1

fun Introspect.toV1(): IntrospectResponseV1 {
    return IntrospectResponseV1(
        active = this.active,
        username = this.username,
        exp = this.exp,
        sub = this.sub,
        roles = this.roles
    )
}
