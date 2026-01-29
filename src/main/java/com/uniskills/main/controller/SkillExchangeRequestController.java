package com.uniskills.main.controller;

import com.uniskills.main.model.SkillExchangeRequest;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.UserRepository;
import com.uniskills.main.service.SkillExchangeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skill-requests")
public class SkillExchangeRequestController {

    private final SkillExchangeRequestService requestService;
    private final UserRepository userRepository;

    public SkillExchangeRequestController(
            SkillExchangeRequestService requestService,
            UserRepository userRepository) {
        this.requestService = requestService;
        this.userRepository = userRepository;
    }

    // Helper: Get Current User ID
    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
    }

    // ✅ 1. SEND REQUEST
    @PostMapping("/{skillId}")
    public ResponseEntity<?> requestSkill(
            @PathVariable Long skillId,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User requester = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(requestService.createRequest(skillId, requester));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ 2. UPDATE STATUS (Accept/Reject)
    // Frontend वरून { "status": "ACCEPTED" } असे JSON पाठवा
    @PutMapping("/{requestId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        return ResponseEntity.ok(requestService.updateStatus(requestId, status));
    }

    // ✅ 3. GET SENT REQUESTS (Outbox)
    @GetMapping("/sent")
    public List<SkillExchangeRequest> getSentRequests(Authentication authentication) {
        return requestService.getMySentRequests(getCurrentUserId(authentication));
    }

    // ✅ 4. GET RECEIVED REQUESTS (Inbox)
    @GetMapping("/received")
    public List<SkillExchangeRequest> getReceivedRequests(Authentication authentication) {
        return requestService.getMyReceivedRequests(getCurrentUserId(authentication));
    }
}