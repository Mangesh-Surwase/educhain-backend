package com.uniskills.main.dto;

public class FeedbackDTO {

    private Long meetingId;
    private Long learnerId;
    private int rating;     // 1 to 5
    private String comment;

    // Getters and Setters
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }

    public Long getLearnerId() { return learnerId; }
    public void setLearnerId(Long learnerId) { this.learnerId = learnerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}