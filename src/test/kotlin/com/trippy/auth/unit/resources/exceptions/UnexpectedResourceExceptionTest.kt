package com.trippy.auth.unit.resources.exceptions

import com.trippy.auth.resources.exceptions.UnexpectedResourceException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class UnexpectedResourceExceptionTest{

    @Test
    fun `given a UnexpectedResourceException, should log a message with the reason and location of the error`(){
        val exception = assertThrows<UnexpectedResourceException> { throw UnexpectedResourceException("test") }

        assertEquals(
            "Unexpected resource exception occurred at (UnexpectedResourceExceptionTest.kt:12): test",
            exception.getLoggedMessage()
        )
    }

    @Test
    fun `given an UnexpectedResourceException with empty stack trace, should return message with null location`() {
        val exception = UnexpectedResourceException("test")
        exception.stackTrace = emptyArray()

        assertEquals(
            "Unexpected resource exception occurred at (null): test",
            exception.getLoggedMessage()
        )
    }
}