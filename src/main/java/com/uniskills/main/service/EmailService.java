package com.uniskills.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Properties मधून व्हॅल्यूज वाचण्यासाठी
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.password}")
    private String senderPassword;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            // 🔥 DEBUG LOGS: हे Render Logs मध्ये बघ
            System.out.println("\n---------- EMAIL DEBUG INFO ----------");
            System.out.println("📧 Sending from: " + senderEmail);
            System.out.println("📤 Sending to: " + toEmail);

            // Password चे पहिले 10 अक्षरे आणि शेवटचे 5 अक्षरे प्रिंट करू (खात्री करण्यासाठी)
            if (senderPassword != null && senderPassword.length() > 15) {
                String start = senderPassword.substring(0, 15);
                String end = senderPassword.substring(senderPassword.length() - 5);
                System.out.println("🔑 Using SMTP Key: " + start + "..." + end);
            } else {
                System.out.println("❌ SMTP Key Not Found or too short!");
            }
            System.out.println("--------------------------------------\n");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("✅ Mail sent successfully to " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
            // Stack Trace प्रिंट करू म्हणजे नेमकं कारण कळेल
            e.printStackTrace();
        }
    }
}