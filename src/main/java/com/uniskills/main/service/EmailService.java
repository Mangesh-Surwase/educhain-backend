package com.uniskills.main.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    // 🔥🔥🔥 डायरेक्ट Key इथेच टाकली आहे (Properties file चा घोळ नको) 🔥🔥🔥
    private final String apiKey = "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";

    private final String brevoApiUrl = "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            System.out.println("--- EMAIL API DEBUG ---");
            System.out.println("Preparing to send email to: " + toEmail);

            // 1. Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ⚠️ महत्त्वाची टीप: Brevo API ला 'api-key' हेच नाव लागतं
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            // 2. Body
            Map<String, Object> payload = new HashMap<>();

            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "EduChain Support");
            sender.put("email", "mangeshsurwase7499@gmail.com"); // Verified Email
            payload.put("sender", sender);

            // Recipient
            Map<String, String> to = new HashMap<>();
            to.put("email", toEmail);
            payload.put("to", List.of(to));

            // Content
            payload.put("subject", subject);
            payload.put("textContent", body);

            // 3. Send
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            System.out.println(payload);
            System.out.println(apiKey);
            System.out.println(headers);



            ResponseEntity<String> response = restTemplate.postForEntity(brevoApiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ SUCCESS! Email sent via API.");
            } else {
                System.err.println("❌ API Error Code: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}