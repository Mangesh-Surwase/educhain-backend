package com.uniskills.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:unknown}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            System.out.println("--- EMAIL DEBUG ---");
            System.out.println("To: " + toEmail);

            if ("unknown".equals(senderEmail)) {
                System.out.println("Sender email property missing. Skipping email.");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("Email sent successfully.");

        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
            // We do NOT throw exception here, so the main thread continues.
        }
    }
}