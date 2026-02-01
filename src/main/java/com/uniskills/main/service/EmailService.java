package com.uniskills.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // हे फक्त Login ID वाचण्यासाठी आहे (याचा वापर setFrom साठी करू नको)
    @Value("${spring.mail.username:unknown}")
    private String brevoLoginId;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            System.out.println("--- EMAIL DEBUG ---");
            System.out.println("To: " + toEmail);
            System.out.println("Using Brevo Account: " + brevoLoginId); // फक्त Debug साठी

            SimpleMailMessage message = new SimpleMailMessage();

            // 🔥🔥🔥 सर्वात महत्त्वाचा बदल (MOST IMPORTANT FIX) 🔥🔥🔥
            // Login ID वापरू नकोस, तुझा Verified Gmail वापर!
            message.setFrom("mangeshsurwase7499@gmail.com");

            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("✅ Email sent successfully!");

        } catch (Exception e) {
            System.err.println("❌ Email failed: " + e.getMessage());
            // ॲप क्रॅश होऊ नये म्हणून Exception इथेच पकडलं आहे.
        }
    }
}