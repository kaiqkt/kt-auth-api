package com.trippy.auth.unit.resources.mail

import com.trippy.auth.resources.exceptions.UnexpectedResourceException
import com.trippy.auth.resources.mail.MailGatewayImpl
import com.trippy.auth.unit.domain.dtos.EmailSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import kotlin.test.Test

class MailGatewayTest {
    private val sender = "test@email.com"
    private val javaMailSender: JavaMailSender = mockk(relaxed = true)
    private val mailGateway = MailGatewayImpl(sender, javaMailSender)


    @Test
    fun `given a email when rendered the template should send successfully`() {
        val email = EmailSampler.sampleVerifyEmail()
        val message = javaMailSender.createMimeMessage()

        justRun { javaMailSender.send(message) }

        mailGateway.send(email)

        verify { javaMailSender.send(message) }
    }

    @Test
    fun `given a email when rendered the template should throw an exception`() {
        val email = EmailSampler.sampleVerifyEmail()
        val message = javaMailSender.createMimeMessage()

        every { javaMailSender.send(message) } throws MailSendException("Fail to send")

        assertThrows<UnexpectedResourceException> { mailGateway.send(email) }

        verify { javaMailSender.send(message) }
    }
}