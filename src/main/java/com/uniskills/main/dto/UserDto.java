package com.uniskills.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    // --- Profile ---
    private String bio;
    private String profileImage;

    // Reputation System Fields ---
    private Double averageRating; // उदा. 4.8
    private Integer totalReviews; // उदा. 15 Reviews
}