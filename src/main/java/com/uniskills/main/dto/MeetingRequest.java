package com.uniskills.main.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class MeetingRequest {


    private Long requestId;

    private String title;
    private String description;
    private LocalDateTime scheduledDate;
    private int duration;
    private String meetingLink;
}