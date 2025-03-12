package com.kaiqkt.auth.domain.exceptions


class DomainException(val type: ErrorType) : Exception(type.message)
