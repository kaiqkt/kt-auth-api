package com.kaiqkt.auth.application.web.responses

import com.kaiqkt.auth.generated.application.web.dtos.PageResponseV1
import org.springframework.data.domain.Page
import java.util.function.Function

fun <T> Page<T>.toV1(mapper: Function<T, Any>) = PageResponseV1(
    totalElements = this.totalElements.toInt(),
    totalPages = this.totalPages,
    currentPage = this.number,
    elements = content.map { mapper.apply(it) }
)
