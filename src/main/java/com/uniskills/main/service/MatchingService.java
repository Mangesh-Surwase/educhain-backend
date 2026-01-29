package com.uniskills.main.service;

import com.uniskills.main.dto.MatchResponse;
import com.uniskills.main.model.Skill;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.SkillRepository;
import com.uniskills.main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchingService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public MatchingService(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    public List<MatchResponse> findMatchesForUser(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get skills the current user wants to LEARN
        List<Skill> learnSkills = skillRepository.findByUser(currentUser)
                .stream()
                .filter(skill -> skill.getType().equalsIgnoreCase("LEARN"))
                .toList();

        List<MatchResponse> matches = new ArrayList<>();

        for (Skill learnSkill : learnSkills) {
            // Find others who TEACH that same skill
            List<Skill> teachingSkills = skillRepository.findByType("TEACH");
            for (Skill teachSkill : teachingSkills) {
                if (teachSkill.getTitle().equalsIgnoreCase(learnSkill.getTitle())
                        && !teachSkill.getUser().getId().equals(userId)) {

                    matches.add(new MatchResponse(
                            teachSkill.getUser().getId(),
                            teachSkill.getUser().getFirstName(),
                            teachSkill.getTitle()
                    ));
                }
            }
        }

        return matches;
    }
}

