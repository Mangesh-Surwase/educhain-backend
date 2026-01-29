package com.uniskills.main.controller;

import com.uniskills.main.dto.DashboardDto;
import com.uniskills.main.model.Meeting;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.MeetingRepository;
import com.uniskills.main.repository.SkillExchangeRequestRepository;
import com.uniskills.main.repository.SkillRepository;
import com.uniskills.main.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillExchangeRequestRepository requestRepository;
    private final MeetingRepository meetingRepository;

    public DashboardController(UserRepository userRepository,
                               SkillRepository skillRepository,
                               SkillExchangeRequestRepository requestRepository,
                               MeetingRepository meetingRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.requestRepository = requestRepository;
        this.meetingRepository = meetingRepository;
    }

    @GetMapping
    public DashboardDto getDashboardStats(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Count Skills
        long totalSkills = skillRepository.countByUser(user);

        // 2. Count Pending Requests (Incoming)
        long pendingRequests = requestRepository.countBySkill_UserAndStatus(user, "PENDING");

        // 3. Count Total Sessions (Completed)
        long totalSessions = meetingRepository.countByMentorOrLearnerAndStatus(user, user, "COMPLETED");

        // 4. Calculate Average Rating (Logic from UserService)
        List<Meeting> ratedMeetings = meetingRepository.findByMentor_IdAndStatus(user.getId(), "COMPLETED");
        double totalRating = 0;
        int count = 0;
        for (Meeting m : ratedMeetings) {
            if (m.getRating() != null && m.getRating() > 0) {
                totalRating += m.getRating();
                count++;
            }
        }
        double avgRating = (count > 0) ? Math.round((totalRating / count) * 10.0) / 10.0 : 0.0;

        // 5. Find Next Upcoming Meeting
        List<Meeting> upcoming = meetingRepository.findUpcomingMeetings(user, LocalDateTime.now());
        Meeting nextMeeting = upcoming.isEmpty() ? null : upcoming.get(0);

        // Build & Return DTO
        return DashboardDto.builder()
                .totalSkills(totalSkills)
                .pendingRequests(pendingRequests)
                .totalSessions(totalSessions)
                .averageRating(avgRating)
                .nextMeeting(nextMeeting)
                .build();
    }
}