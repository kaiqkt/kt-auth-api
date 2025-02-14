package com.trippy.auth.resources.mail

import com.trippy.auth.domain.dtos.Email
import com.trippy.auth.domain.gateways.MailGateway
import com.trippy.auth.resources.exceptions.UnexpectedResourceException
import org.apache.commons.text.StringSubstitutor
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.nio.file.Files
import kotlin.io.inputStream
import kotlin.text.replace

@Component
class MailGatewayImpl(
    @Value("\${spring.mail.username}")
    private val sender: String,
    private val javaMailSender: JavaMailSender
) : MailGateway {

    override fun send(email: Email) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        try {
            val content = renderTemplate(email)

            helper.setTo(email.recipient)
            helper.setFrom(sender)
            helper.setSubject(email.subject)
            helper.setText(content, true)

            javaMailSender.send(message)
        } catch (ex: MailException) {
            throw UnexpectedResourceException("Failed to send email, error: $ex")
        }
    }

    private fun renderTemplate(email: Email): String {
        val inputStream =  ClassPathResource(email.template).inputStream
        val template = inputStream.bufferedReader().use { it.readText() }

        return StringSubstitutor(email.data).replace(template)
    }
}