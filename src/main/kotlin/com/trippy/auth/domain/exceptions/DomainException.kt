package com.trippy.auth.domain.exceptions


class DomainException(val type: ErrorType) : Exception(type.message)