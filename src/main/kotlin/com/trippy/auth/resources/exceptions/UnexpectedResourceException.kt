package com.trippy.auth.resources.exceptions

import org.slf4j.LoggerFactory

class UnexpectedResourceException(message: String) : Exception(message) {
    init {
        LoggerFactory.getLogger(UnexpectedResourceException::class.java).error(getLoggedMessage())
    }

    fun sourceLocation(): String? {
        val stackElement = this.stackTrace.firstOrNull() ?: return null

        return "${stackElement.fileName}:${stackElement.lineNumber}"
    }

    fun getLoggedMessage(): String {
        return "Unexpected resource exception occurred at (${sourceLocation()}): $message"
    }
}