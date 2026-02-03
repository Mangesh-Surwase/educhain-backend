package com.uniskills.main.service;

import com.uniskills.main.dto.CreateSkillRequest;
import com.uniskills.main.dto.SkillDto; // 🔥 Import
import com.uniskills.main.dto.UserDto;
import com.uniskills.main.model.Meeting;
import com.uniskills.main.model.Skill;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.MeetingRepository;
import com.uniskills.main.repository.SkillExchangeRequestRepository;
import com.uniskills.main.repository.SkillRepository;
import com.uniskills.main.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillExchangeRequestRepository requestRepository;
    private final MeetingRepository meetingRepository;

    public SkillService(SkillRepository skillRepository,
                        UserRepository userRepository,
                        SkillExchangeRequestRepository requestRepository,
                        MeetingRepository meetingRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.meetingRepository = meetingRepository;
    }

    // ADD SKILL
    public Skill addSkill(CreateSkillRequest req, User user) {
        Skill skill = new Skill();
        skill.setTitle(req.getTitle());
        skill.setDescription(req.getDescription());
        skill.setCategory(req.getCategory());
        skill.setProficiency(req.getProficiency());
        skill.setTags(req.getTags());
        skill.setType(req.getType());
        skill.setUser(user);
        return skillRepository.save(skill);
    }

    //  GET SKILLS BY USER
    public List<Skill> getUserSkills(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return skillRepository.findByUser(user);
    }

    // GET ALL SKILLS
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    // DELETE SKILL (With Cleanup)
    @Transactional
    public void deleteSkill(Long id) {
        meetingRepository.deleteBySkill_Id(id);
        requestRepository.deleteBySkill_Id(id);
        skillRepository.deleteById(id);
    }

    //  UPDATE SKILL
    public Skill updateSkill(Long id, CreateSkillRequest req) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        skill.setTitle(req.getTitle());
        skill.setDescription(req.getDescription());
        skill.setCategory(req.getCategory());
        skill.setProficiency(req.getProficiency());
        skill.setTags(req.getTags());
        skill.setType(req.getType());
        return skillRepository.save(skill);
    }

    //  BASIC SEARCH
    public List<Skill> searchSkills(String title, String type, String category) {
        if (title != null && !title.isEmpty()) {
            return skillRepository.findByTitleContainingIgnoreCase(title);
        }
        if (type != null && !type.isEmpty()) {
            return skillRepository.findByType(type);
        }
        return skillRepository.findAll();
    }

    // 🔥🔥🔥 SMART EXPLORE SKILLS (With Rating Calculation) 🔥🔥🔥
    // आता हे Function List<Skill> ऐवजी List<SkillDto> रिटर्न करेल
    public List<SkillDto> exploreSkills(String query, String currentUserEmail) {

        // 1. Current User आणि त्याचा Role शोधणे
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // MENTOR असेल तर LEARN स्किल्स दिसतील, नाहीतर TEACH
        String targetType = "MENTOR".equalsIgnoreCase(currentUser.getRole()) ? "LEARN" : "TEACH";

        // 2. Skills शोधणे (Query किंवा Type नुसार)
        List<Skill> skills;
        if (query != null && !query.trim().isEmpty()) {
            skills = skillRepository.findByTitleContainingIgnoreCaseAndType(query, targetType);
        } else {
            skills = skillRepository.findByType(targetType);
        }

        // 3. Convert Skill -> SkillDto (Add Rating Calculation)
        List<SkillDto> dtos = new ArrayList<>();

        for (Skill skill : skills) {
            User skillOwner = skill.getUser();

            // 🔥 RATING CALCULATION LOGIC 🔥
            // या User च्या (Mentor म्हणून) सर्व COMPLETED मीटिंग्स शोधा
            List<Meeting> completedMeetings = meetingRepository.findByMentor_IdAndStatus(skillOwner.getId(), "COMPLETED");

            double totalRating = 0;
            int reviewCount = 0;
            for (Meeting m : completedMeetings) {
                if (m.getRating() != null && m.getRating() > 0) {
                    totalRating += m.getRating();
                    reviewCount++;
                }
            }
            // Average काढणे (1 decimal place)
            double averageRating = (reviewCount > 0) ? Math.round((totalRating / reviewCount) * 10.0) / 10.0 : 0.0;

            // 🔥 Create UserDto (Rating सह)
            UserDto userDto = UserDto.builder()
                    .id(skillOwner.getId())
                    .firstName(skillOwner.getFirstName())
                    .lastName(skillOwner.getLastName())
                    .email(skillOwner.getEmail())
                    .role(skillOwner.getRole())
                    .bio(skillOwner.getBio())
                    .profileImage(skillOwner.getProfileImage())
                    .averageRating(averageRating) // ✅ Rating Added Here
                    .totalReviews(reviewCount)    // ✅ Count Added Here
                    .build();

            // 🔥 Create SkillDto
            SkillDto dto = SkillDto.builder()
                    .id(skill.getId())
                    .title(skill.getTitle())
                    .description(skill.getDescription())
                    .category(skill.getCategory())
                    .proficiency(skill.getProficiency())
                    .tags(skill.getTags())
                    .type(skill.getType())
                    .createdAt(skill.getCreatedAt())
                    .user(userDto) // ✅ Enriched User Object Set केला
                    .build();

            dtos.add(dto);
        }

        return dtos;
    }
}