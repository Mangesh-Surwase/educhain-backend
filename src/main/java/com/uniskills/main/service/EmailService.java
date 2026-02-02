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

    // application.properties मधून API Key वाचतो
    @Value("${brevo.api.key}")
    private String apiKey;

    // Brevo ची फिक्स URL
    private final String brevoApiUrl = "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            System.out.println("--- EMAIL API DEBUG ---");
            System.out.println("Preparing to send email to: " + toEmail);

            // 1. Headers सेट करणे (API Key इथे जाते)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            // 2. Body (JSON Payload) तयार करणे
            Map<String, Object> payload = new HashMap<>();

            // Sender (तुझा Verified Email)
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "EduChain Support");
            sender.put("email", "mangeshsurwase7499@gmail.com"); // 🔥 हा तुझा Verified Email आहे
            payload.put("sender", sender);

            // Recipient (ज्याला पाठवायचा आहे)
            Map<String, String> to = new HashMap<>();
            to.put("email", toEmail);
            payload.put("to", List.of(to));

            // Subject & Content
            payload.put("subject", subject);
            payload.put("textContent", body); // साध्या टेक्स्टसाठी
            // तुला HTML पाठवायचा असेल तर खालील ओळ Uncomment कर:
            // payload.put("htmlContent", "<h1>" + body + "</h1>");

            // 3. Request पाठवणे (POST Call)
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(brevoApiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Email Sent Successfully via API! Response: " + response.getBody());
            } else {
                System.err.println("❌ API Error: " + response.getStatusCode());
                System.err.println("Response Body: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("❌ Email Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}