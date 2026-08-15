package com.terangalink.backend.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    private JavaMailSender javaMailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        javaMailSender = Mockito.mock(JavaMailSender.class);
        emailService = new EmailService(javaMailSender, "noreply@example.com");
    }

    @Test
    void sendVerificationEmail_shouldBuildMimeMessage_andSend() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // No exception expected
        emailService.sendVerificationEmail("user@example.com", "Alice", "123456");

        // Verify fields on mimeMessage
        assertThat(mimeMessage.getAllRecipients()).isNotNull();
        assertThat(mimeMessage.getAllRecipients()[0].toString()).isEqualTo("user@example.com");
        assertThat(mimeMessage.getSubject()).isEqualTo("Bienvenue sur TerangaLink - Verifiez votre adresse email");
        String content = (String) mimeMessage.getContent();
        assertThat(content).contains("123456");
    }

    @Test
    void sendVerificationEmail_shouldPropagateOnSendFailure() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("send failed")).when(javaMailSender).send(any(MimeMessage.class));

        assertThrows(MailSendException.class,
                () -> emailService.sendVerificationEmail("user@example.com", "Alice", "123456"));
    }
}
