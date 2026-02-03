package com.uniskills.main.service;

import com.uniskills.main.model.Skill;
import com.uniskills.main.model.SkillExchangeRequest;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.SkillExchangeRequestRepository;
import com.uniskills.main.repository.SkillRepository;
import com.uniskills.main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillExchangeRequestService {

    private final SkillRepository skillRepository;
    private final SkillExchangeRequestRepository requestRepository;
    private final UserRepository userRepository;

    //  New Dependency
    private final NotificationService notificationService;

    public SkillExchangeRequestService(
            SkillRepository skillRepository,
            SkillExchangeRequestRepository requestRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.skillRepository = skillRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    //  1. CREATE REQUEST
    public SkillExchangeRequest createRequest(Long skillId, User requester) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (skill.getUser().getId().equals(requester.getId())) {
            throw new RuntimeException("You cannot request your own skill!");
        }

        if (requestRepository.existsByRequesterAndSkill(requester, skill)) {
            throw new RuntimeException("Request already sent for this skill.");
        }

        SkillExchangeRequest request = new SkillExchangeRequest();
        request.setSkill(skill);
        request.setRequester(requester);
        request.setStatus("PENDING");

        SkillExchangeRequest savedRequest = requestRepository.save(request);

        // TRIGGER NOTIFICATION

        String msg = "New request from " + requester.getFirstName() + " for your skill: " + skill.getTitle();
        notificationService.sendNotification(skill.getUser(), msg);

        return savedRequest;
    }

    // 2. UPDATE STATUS
    public SkillExchangeRequest updateStatus(Long requestId, String status) {
        SkillExchangeRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        req.setStatus(status.toUpperCase());
        SkillExchangeRequest savedReq = requestRepository.save(req);

        //  TRIGGER NOTIFICATION

        String msg = "Your request for " + req.getSkill().getTitle() + " has been " + status.toUpperCase();
        notificationService.sendNotification(req.getRequester(), msg);

        return savedReq;
    }

    //  3. GET SENT REQUESTS
    public List<SkillExchangeRequest> getMySentRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return requestRepository.findByRequester(user);
    }

    //  4. GET RECEIVED REQUESTS
    public List<SkillExchangeRequest> getMyReceivedRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return requestRepository.findBySkill_User(user);
    }
}