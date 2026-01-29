package com.uniskills.main.service;

import com.uniskills.main.dto.UserDto;
import com.uniskills.main.model.Meeting;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.MeetingRepository;
import com.uniskills.main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    public UserService(UserRepository userRepository, MeetingRepository meetingRepository) {
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
    }

    // ✅ 1. Get User Profile (With Rating Calculation)
    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥🔥🔥 RATING CALCULATION START 🔥🔥🔥
        List<Meeting> completedMeetings = meetingRepository.findByMentor_IdAndStatus(userId, "COMPLETED");

        double totalRating = 0;
        int reviewCount = 0;

        for (Meeting m : completedMeetings) {
            if (m.getRating() != null && m.getRating() > 0) {
                totalRating += m.getRating();
                reviewCount++;
            }
        }

        double averageRating = 0.0;
        if (reviewCount > 0) {
            averageRating = totalRating / reviewCount;
            averageRating = Math.round(averageRating * 10.0) / 10.0;
        }
        // 🔥🔥🔥 RATING CALCULATION END 🔥🔥🔥

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                // .credits(user.getCredits()) ❌ REMOVED
                .averageRating(averageRating) // ✅ ADDED
                .totalReviews(reviewCount)    // ✅ ADDED
                .build();
    }

    // ✅ 2. Update Profile
    public UserDto updateProfile(Long userId, UserDto req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getBio() != null) user.setBio(req.getBio());
        if (req.getProfileImage() != null) user.setProfileImage(req.getProfileImage());
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());

        User savedUser = userRepository.save(user);
        return getUserProfile(savedUser.getId());
    }
}