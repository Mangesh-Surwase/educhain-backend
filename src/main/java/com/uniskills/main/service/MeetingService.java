package com.uniskills.main.service;

import com.uniskills.main.dto.MeetingRequest;
import com.uniskills.main.model.Meeting;
import com.uniskills.main.model.SkillExchangeRequest;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.MeetingRepository;
import com.uniskills.main.repository.SkillExchangeRequestRepository;
import com.uniskills.main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final SkillExchangeRequestRepository requestRepository;
    private final UserRepository userRepository;

    // New Dependency
    private final NotificationService notificationService;

    public MeetingService(MeetingRepository meetingRepository,
                          SkillExchangeRequestRepository requestRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.meetingRepository = meetingRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // CREATE OR UPDATE MEETING
    public Meeting createMeeting(MeetingRequest req, String userEmail) {

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SkillExchangeRequest request = requestRepository.findById(req.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"ACCEPTED".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Cannot schedule meeting. Request is not ACCEPTED.");
        }

        //  SMART MENTOR LOGIC
        User mentor;
        User learner;
        String skillType = request.getSkill().getType();

        if ("LEARN".equalsIgnoreCase(skillType)) {
            mentor = request.getRequester();
            learner = request.getSkill().getUser();
        } else {
            mentor = request.getSkill().getUser();
            learner = request.getRequester();
        }

        if (!currentUser.getId().equals(mentor.getId())) {
            throw new RuntimeException("Permission Denied: Only the Mentor can schedule this session.");
        }

        Optional<Meeting> existingMeeting = meetingRepository.findByRequest_Id(req.getRequestId());

        Meeting meeting;
        boolean isReschedule = false;

        if (existingMeeting.isPresent()) {
            meeting = existingMeeting.get();
            isReschedule = true;
        } else {
            meeting = new Meeting();
            meeting.setRequest(request);
            meeting.setSkill(request.getSkill());
            meeting.setMentor(mentor);
            meeting.setLearner(learner);
            meeting.setStatus("SCHEDULED");
        }

        meeting.setTitle(req.getTitle());
        meeting.setDescription(req.getDescription());
        meeting.setMeetingLink(req.getMeetingLink());
        meeting.setScheduledDate(req.getScheduledDate());
        meeting.setDuration(req.getDuration());

        Meeting savedMeeting = meetingRepository.save(meeting);


        String action = isReschedule ? "rescheduled" : "scheduled";
        String dateStr = req.getScheduledDate().toString().replace("T", " at ");

        // 1. Notify Learner
        notificationService.sendNotification(learner,
                "A session for '" + request.getSkill().getTitle() + "' has been " + action + " on " + dateStr);

        // 2. Notify Mentor (Confirmation)
        notificationService.sendNotification(mentor,
                "You successfully " + action + " the session for '" + request.getSkill().getTitle() + "'");

        return savedMeeting;
    }

    //  GET USER MEETINGS
    public List<Meeting> getUserMeetings(Long userId) {
        return meetingRepository.findAll().stream()
                .filter(m -> m.getMentor().getId().equals(userId) || m.getLearner().getId().equals(userId))
                .toList();
    }

    public Meeting getMeetingById(Long id) {
        return meetingRepository.findById(id).orElseThrow(() -> new RuntimeException("Meeting not found"));
    }

    public Meeting saveMeeting(Meeting meeting) { return meetingRepository.save(meeting); }

    public Meeting updateMeetingStatus(Long id, String status) {
        Meeting m = getMeetingById(id);
        m.setStatus(status);
        return meetingRepository.save(m);
    }

    //  COMPLETE MEETING
    public Meeting completeMeeting(Long id, Integer rating, String feedback, String userEmail) {
        Meeting m = getMeetingById(id);
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getId().equals(m.getLearner().getId())) {
            throw new RuntimeException("Permission Denied: Only the Student/Learner can mark the session as completed.");
        }

        if(rating != null && (rating < 1 || rating > 5)) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        m.setStatus("COMPLETED");
        m.setRating(rating);
        m.setFeedback(feedback);

        Meeting savedMeeting = meetingRepository.save(m);


        String msg = "Your session for '" + m.getSkill().getTitle() + "' was marked complete! You got " + rating + " Stars ⭐";
        notificationService.sendNotification(m.getMentor(), msg);

        return savedMeeting;
    }
}