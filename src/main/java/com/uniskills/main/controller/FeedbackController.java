package com.uniskills.main.controller;

import com.uniskills.main.dto.FeedbackDTO;
import com.uniskills.main.model.Feedback;
import com.uniskills.main.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")

public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // Add Feedback
    @PostMapping("/add")
    public ResponseEntity<?> addFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            String response = feedbackService.addFeedback(feedbackDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get Reviews for Mentor
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<?> getMentorFeedback(@PathVariable Long mentorId) {
        List<Feedback> reviews = feedbackService.getFeedbackByMentor(mentorId);
        return ResponseEntity.ok(reviews);
    }
}