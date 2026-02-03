package com.uniskills.main.service;

import org.springframework.beans.factory.annotation.Value;
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

    // Reading from application.properties -> Render Environment
    @Value("${brevo.api.key}")
    private String apiKey;

    private final String brevoApiUrl = "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            System.out.println("--- EMAIL API DEBUG ---");
            // Security: Masking key in logs
            String maskedKey = (apiKey != null && apiKey.length() > 4) ? apiKey.substring(0, 4) + "..." : "null";
            System.out.println("Sending email to: " + toEmail + " using Key: " + maskedKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            Map<String, Object> payload = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", "EduChain Support");
            sender.put("email", "mangeshsurwase7499@gmail.com");
            payload.put("sender", sender);

            Map<String, String> to = new HashMap<>();
            to.put("email", toEmail);
            payload.put("to", List.of(to));

            payload.put("subject", subject);
            payload.put("textContent", body);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(brevoApiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ SUCCESS! Email sent via API.");
            } else {
                System.err.println("❌ API Error Code: " + response.getStatusCode());
                System.err.println("❌ API Response: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}