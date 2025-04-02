package com.kaiqkt.auth.application.web.controllers

import com.kaiqkt.auth.application.web.responses.toV1
import com.kaiqkt.auth.domain.models.Session
import com.kaiqkt.auth.domain.services.SessionService
import com.kaiqkt.auth.domain.utils.Constants
import com.kaiqkt.auth.generated.application.web.controllers.SessionApi
import com.kaiqkt.auth.generated.application.web.controllers.SessionsApi
import com.kaiqkt.auth.generated.application.web.dtos.IdsRequestV1
import com.kaiqkt.auth.generated.application.web.dtos.PageResponseV1
import com.kaiqkt.kt.tools.security.utils.ContextUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class SessionController(private val sessionService: SessionService) : SessionsApi, SessionApi {

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    override fun getAll(
        page: Int,
        size: Int,
        sort: String,
        orderBy: String,
        id: String?
    ): ResponseEntity<PageResponseV1> {
        val roles = ContextUtils.getValue(Constants.ROLES, List::class.java)
        val searchId =
            if (roles.contains(Constants.ROLE_ADMIN)) id else ContextUtils.getValue(Constants.USER_ID, String::class.java)

        val pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(sort), orderBy))
        val response = sessionService.search(searchId, pageRequest)

        return ResponseEntity.ok(response.toV1(Session::toV1))
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    override fun revoke(sessionId: String): ResponseEntity<Unit> {
        val userId = ContextUtils.getValue(Constants.USER_ID, String::class.java)
        sessionService.revokeById(sessionId, userId)

        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    override fun revokeCurrent(): ResponseEntity<Unit> {
        val userId = ContextUtils.getValue(Constants.USER_ID, String::class.java)
        val sessionId = ContextUtils.getValue(Constants.SESSION_ID, String::class.java)
        sessionService.revokeById(sessionId, userId)

        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    override fun revokeAll(): ResponseEntity<Unit> {
        val userId = ContextUtils.getValue(Constants.USER_ID, String::class.java)
        sessionService.revokeAllByUserId(userId)

        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun revokeAllByIds(idsRequestV1: IdsRequestV1): ResponseEntity<Unit> {
        sessionService.revokeAllByIds(idsRequestV1.ids)

        return ResponseEntity.noContent().build()
    }
}
