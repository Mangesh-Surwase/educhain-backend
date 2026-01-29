package com.uniskills.main.dto;

import com.uniskills.main.model.Meeting;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDto {
    // 📊 Top Stats Cards
    private long totalSkills;       // पोस्ट केलेली स्किल्स
    private long totalSessions;     // घेतलेले/दिलेले सेशन्स (Completed)
    private long pendingRequests;   // आलेल्या नवीन रिक्वेस्ट्स
    private double averageRating;   // स्टार रेटिंग

    // 🗓️ Upcoming Action
    private Meeting nextMeeting;    // सर्वात जवळची नियोजित मिटिंग (जर असेल तर)
}