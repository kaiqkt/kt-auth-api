package com.kaiqkt.auth.unit.application.web.handler

import com.kaiqkt.auth.application.web.handler.ErrorHandler
import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import io.mockk.every
import io.mockk.mockk
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest
import kotlin.test.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import com.kaiqkt.auth.generated.application.web.dtos.ErrorV1

class ErrorHandlerTest {
    private val webRequest: WebRequest = mockk()
    private val errorHandler = ErrorHandler()

    @BeforeEach
    fun beforeAll() {
        every { webRequest.getDescription(any()) } returns "description"
    }

    @Test
    fun `given an domain exception when handling should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.INVALID_ARGUMENTS)

        val response = errorHandler.handleDomainException(domainException, webRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorType.INVALID_ARGUMENTS.name, response.body?.type)
        assertEquals("Invalid arguments", response.body?.message)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `given an method argument not valid exception when handling should return and the message in the exception`() {
        val methodArgumentNotValidException = mockk<MethodArgumentNotValidException>()
        val fieldError = mockk<FieldError>()

        every { fieldError.field } returns "field"
        every { fieldError.defaultMessage } returns "defaultMessage"
        every { methodArgumentNotValidException.bindingResult.allErrors } returns listOf(fieldError)
        every { methodArgumentNotValidException.message } returns "Invalid arguments"


        val response = errorHandler.handleMethodArgumentNotValid(
            methodArgumentNotValidException,
            HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            webRequest
        ) as ResponseEntity<ErrorV1>

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorType.INVALID_ARGUMENTS.name, response.body?.type)
        assertEquals("Invalid arguments", response.body?.message)
        assertEquals("defaultMessage", response.body?.errors?.get("field"))
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `given an method argument not valid exception when handling should return and message null in the exception`() {
        val methodArgumentNotValidException = mockk<MethodArgumentNotValidException>()
        val fieldError = mockk<FieldError>()

        every { fieldError.field } returns "field"
        every { fieldError.defaultMessage } returns null
        every { methodArgumentNotValidException.bindingResult.allErrors } returns listOf(fieldError)
        every { methodArgumentNotValidException.message } returns "Invalid arguments"


        val response = errorHandler.handleMethodArgumentNotValid(
            methodArgumentNotValidException,
            HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            webRequest
        ) as ResponseEntity<ErrorV1>

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorType.INVALID_ARGUMENTS.name, response.body?.type)
        assertEquals("Invalid arguments", response.body?.message)
        assertEquals(null, response.body?.errors?.get("field"))
    }

    @Test
    fun `given an exception when handling should return the message in the exception`() {
        val exception = Exception("message")

        val response = errorHandler.handleGenericException(exception, webRequest)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(ErrorType.INTERNAL_SERVER_ERROR.name, response.body?.type)
        assertEquals("message", response.body?.message)
    }

    @Test
    fun `given an constraint violation exception should return the constraint violations`(){
        val constraintViolationException = mockk<ConstraintViolationException>()
        val constraintViolation = mockk<ConstraintViolation<*>>()

        every { constraintViolation.propertyPath.toString() } returns "field"
        every { constraintViolation.message } returns "message"
        every { constraintViolationException.constraintViolations } returns setOf(constraintViolation)
        every { constraintViolationException.message } returns "Invalid arguments"

        val response = errorHandler.handleConstraintViolationException(constraintViolationException, webRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorType.INVALID_ARGUMENTS.name, response.body?.type)
        assertEquals("Invalid arguments", response.body?.message)
        assertEquals("message", response.body?.errors?.get("field"))
    }

    @Test
    fun `given an exception without message when handling should return the message in the exception`() {
        val exception = Exception()

        val response = errorHandler.handleGenericException(exception, webRequest)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(ErrorType.INTERNAL_SERVER_ERROR.name, response.body?.type)
        assertEquals("Internal server error", response.body?.message)
    }
}
