package com.trippy.auth.unit.application.web.dtos

import com.trippy.auth.generated.application.web.dtos.PageResponseV1

object PageResponseSampler {
    fun sample(elements: List<Any> = listOf(1, 2, 3)) = PageResponseV1(
        totalPages = 1,
        totalElements = 3,
        elements = elements
    )
}