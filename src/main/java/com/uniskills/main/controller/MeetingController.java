package com.uniskills.main.controller;

import com.uniskills.main.dto.MeetingRequest;
import com.uniskills.main.model.Meeting;
import com.uniskills.main.service.MeetingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }


    @PostMapping
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequest meetingRequest, Authentication authentication) {
        try {

            String email = authentication.getName();
            Meeting meeting = meetingService.createMeeting(meetingRequest, email);

            return ResponseEntity.ok(Map.of(
                    "message", "Meeting scheduled successfully!",
                    "meetingId", meeting.getId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 Bad Request
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/user/{userId}")
    public List<Meeting> getUserMeetings(@PathVariable Long userId) {
        return meetingService.getUserMeetings(userId);
    }


    @PutMapping("/{id}/status")
    public Meeting updateMeetingStatus(@PathVariable Long id, @RequestParam String status) {
        return meetingService.updateMeetingStatus(id, status);
    }


    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeMeeting(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        try {
            Integer rating = (Integer) payload.get("rating");
            String feedback = (String) payload.get("feedback");
            String email = authentication.getName(); // 🔥 Identify User

            return ResponseEntity.ok(meetingService.completeMeeting(id, rating, feedback, email));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleMeeting(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Meeting meeting = meetingService.getMeetingById(id);

            if (updates.containsKey("scheduled_date")) {
                meeting.setScheduledDate(LocalDateTime.parse((String) updates.get("scheduled_date")));
            }

            if (updates.containsKey("duration")) {
                Object durationObj = updates.get("duration");
                if (durationObj instanceof Integer) {
                    meeting.setDuration((Integer) durationObj);
                } else {
                    meeting.setDuration(Integer.parseInt(String.valueOf(durationObj)));
                }
            }

            return ResponseEntity.ok(meetingService.saveMeeting(meeting));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}