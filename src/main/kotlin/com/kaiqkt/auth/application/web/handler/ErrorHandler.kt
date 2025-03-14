package com.kaiqkt.auth.application.web.handler

import com.kaiqkt.auth.domain.exceptions.DomainException
import com.kaiqkt.auth.domain.exceptions.ErrorType
import com.kaiqkt.auth.generated.application.web.dtos.ErrorV1
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import kotlin.code

@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException, request: WebRequest): ResponseEntity<ErrorV1> {
        val error = ErrorV1(ex.type.name, ex.message)

        log(ex, request.getDescription(false))

        return ResponseEntity(error, HttpStatusCode.valueOf(ex.type.code))
    }

    public override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<in Any>? {
        val errors: MutableMap<String, String> = HashMap()

        ex.bindingResult.allErrors.forEach { error ->
            val fieldError = error as FieldError

            if (fieldError.defaultMessage != null) {
                errors[fieldError.field] = fieldError.defaultMessage!!
            }
        }

        val error = ErrorV1(
            type = ErrorType.INVALID_ARGUMENTS.name,
            message = "Invalid arguments",
            errors = errors.toMap()
        )

        log(ex, request.getDescription(false))

        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest
    ): ResponseEntity<ErrorV1> {
        val errors: MutableMap<String, Any> = HashMap()

        ex.constraintViolations.forEach { error ->
            val propertyPath = error.propertyPath.toString().substringAfter(".")
            errors[propertyPath] = error.message
        }

        val error = ErrorV1(
            type = ErrorType.INVALID_ARGUMENTS.name,
            message = "Invalid arguments",
            errors = errors.toMap()
        )

        log(ex, request.getDescription(false))

        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    private fun log(ex: Exception, description: String) {
        val log = LoggerFactory.getLogger(ErrorHandler::class.java)

        log.error("Error: {}, Message: {}, Request: {}", ex, ex.message, description)
    }
}
