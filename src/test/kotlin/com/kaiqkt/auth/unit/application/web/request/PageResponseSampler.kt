package com.kaiqkt.auth.unit.application.web.request

import com.kaiqkt.auth.generated.application.web.dtos.PageResponseV1

object PageResponseSampler {
    fun sample(elements: List<Any> = listOf(1, 2, 3)) = PageResponseV1(
        totalPages = 1,
        totalElements = 3,
        elements = elements
    )
}
