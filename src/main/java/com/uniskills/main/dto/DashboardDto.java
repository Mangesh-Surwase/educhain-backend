package com.uniskills.main.dto;

import com.uniskills.main.model.Meeting;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDto {

    private long totalSkills;
    private long totalSessions;
    private long pendingRequests;
    private double averageRating;


    private Meeting nextMeeting;
}