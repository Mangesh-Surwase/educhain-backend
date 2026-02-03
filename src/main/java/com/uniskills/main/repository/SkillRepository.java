package com.uniskills.main.repository;

import com.uniskills.main.model.Skill;
import com.uniskills.main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUser(User user);
    List<Skill> findByType(String type);
    List<Skill> findByTitleContainingIgnoreCase(String title);
    List<Skill> findByCategory(String category);
    List<Skill> findByTitleContainingIgnoreCaseAndType(String title, String type);


    long countByUser(User user);
}