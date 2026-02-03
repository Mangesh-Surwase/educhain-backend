package com.uniskills.main.service;

import com.uniskills.main.dto.FeedbackDTO;
import com.uniskills.main.model.Feedback;
import com.uniskills.main.model.Meeting;
import com.uniskills.main.repository.FeedbackRepository;
import com.uniskills.main.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    public String addFeedback(FeedbackDTO feedbackDTO) {


        Meeting meeting = meetingRepository.findById(feedbackDTO.getMeetingId())
                .orElseThrow(() -> new RuntimeException("Meeting ID not found"));


        if (!"COMPLETED".equalsIgnoreCase(meeting.getStatus())) {
            throw new RuntimeException("Meeting is not completed yet. You cannot give feedback.");
        }


        if (!meeting.getLearner().getId().equals(feedbackDTO.getLearnerId())) {
            throw new RuntimeException("This learner was not part of this meeting.");
        }


        Feedback feedback = new Feedback();
        feedback.setMeetingId(feedbackDTO.getMeetingId());
        feedback.setLearnerId(feedbackDTO.getLearnerId());
        feedback.setRating(feedbackDTO.getRating());
        feedback.setComment(feedbackDTO.getComment());


        feedback.setMentorId(meeting.getMentor().getId());

        feedbackRepository.save(feedback);

        return "Feedback submitted successfully!";
    }

    public List<Feedback> getFeedbackByMentor(Long mentorId) {
        return feedbackRepository.findByMentorId(mentorId);
    }
}