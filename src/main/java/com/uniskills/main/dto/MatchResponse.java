package com.uniskills.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponse {
    private Long userId;
    private String userName;
    private String skillName;

    public MatchResponse(Long userId, String userName, String skillName) {
        this.userId = userId;
        this.userName = userName;
        this.skillName = skillName;
    }
}

