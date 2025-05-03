package com.martial.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.domain.url}")
    private String domainUrl;

    public void sendVerificationEmail(String email, String token, String redirectUrl) {
        String verificationLink = redirectUrl != null ? redirectUrl + "?token=" + token
                : domainUrl + "/api/v1/auth/verify?token=" + token;

        String emailContent = String.format(
                "Please click the link below to verify your email:\n%s",
                verificationLink);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setText(emailContent);

        mailSender.send(message);
    }
}