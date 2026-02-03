package com.uniskills.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String proficiency;
    private List<String> tags;
    private String type; // TEACH or LEARN
    private LocalDateTime createdAt;


    private UserDto user;
}