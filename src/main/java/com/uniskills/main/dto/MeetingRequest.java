package com.uniskills.main.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class MeetingRequest {

    // 🔥 फक्त Request ID पुरेसा आहे, बाकी आपण शोधू
    private Long requestId;

    private String title;
    private String description;
    private LocalDateTime scheduledDate;
    private int duration;
    private String meetingLink;
}