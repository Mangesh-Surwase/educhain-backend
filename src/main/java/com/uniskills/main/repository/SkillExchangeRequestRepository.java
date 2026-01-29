package com.uniskills.main.repository;

import com.uniskills.main.model.Skill;
import com.uniskills.main.model.SkillExchangeRequest;
import com.uniskills.main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillExchangeRequestRepository extends JpaRepository<SkillExchangeRequest, Long> {

    List<SkillExchangeRequest> findByRequester(User user);

    List<SkillExchangeRequest> findBySkill_User(User user);

    boolean existsByRequesterAndSkill(User requester, Skill skill);

    void deleteBySkill_Id(Long skillId);

    // 🔥🔥🔥 NEW: Dashboard साठी
    // मला आलेल्या (Received) आणि अजूनही 'PENDING' असलेल्या रिक्वेस्ट मोजणे.
    long countBySkill_UserAndStatus(User user, String status);
}