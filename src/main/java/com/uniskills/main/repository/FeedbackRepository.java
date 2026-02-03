package com.uniskills.main.repository;

import com.uniskills.main.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByMentorId(Long mentorId);
}