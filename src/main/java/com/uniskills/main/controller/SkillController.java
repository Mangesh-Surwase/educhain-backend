package com.uniskills.main.controller;

import com.uniskills.main.dto.CreateSkillRequest;
import com.uniskills.main.dto.SkillDto; // 🔥 Import
import com.uniskills.main.model.Skill;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.UserRepository;
import com.uniskills.main.service.SkillService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;
    private final UserRepository userRepository;

    public SkillController(SkillService skillService,
                           UserRepository userRepository) {
        this.skillService = skillService;
        this.userRepository = userRepository;
    }

    // ✅ ADD SKILL
    @PostMapping
    public Skill addSkill(@RequestBody CreateSkillRequest request,
                          Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return skillService.addSkill(request, user);
    }

    // ✅ GET ALL SKILLS
    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    // ✅ GET SKILLS BY USER
    @GetMapping("/user/{userId}")
    public List<Skill> getUserSkills(@PathVariable Long userId) {
        return skillService.getUserSkills(userId);
    }

    // ✅ UPDATE SKILL
    @PutMapping("/{id}")
    public Skill updateSkill(@PathVariable Long id,
                             @RequestBody CreateSkillRequest request) {
        return skillService.updateSkill(id, request);
    }

    // ✅ DELETE SKILL
    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
    }

    // ✅ BASIC SEARCH
    @GetMapping("/search")
    public List<Skill> searchSkills(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {
        return skillService.searchSkills(title, type, category);
    }

    // 🔥🔥🔥 UPDATED EXPLORE API 🔥🔥🔥
    // आता ही API SkillDto रिटर्न करेल ज्यामध्ये Rating असेल
    @GetMapping("/explore")
    public List<SkillDto> exploreSkills(
            @RequestParam(required = false) String query,
            Authentication authentication) {

        String email = authentication.getName();
        // Service मध्ये आपण आता डायरेक्ट Email पाठवत आहोत
        return skillService.exploreSkills(query, email);
    }
}