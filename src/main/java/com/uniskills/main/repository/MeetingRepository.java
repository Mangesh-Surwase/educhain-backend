package com.uniskills.main.repository;

import com.uniskills.main.model.Meeting;
import com.uniskills.main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByMentorOrLearner(User mentor, User learner);

    Optional<Meeting> findByRequest_Id(Long requestId);

    void deleteBySkill_Id(Long skillId);


    List<Meeting> findByMentor_IdAndStatus(Long mentorId, String status);


    long countByMentorOrLearnerAndStatus(User mentor, User learner, String status);


    @Query("SELECT m FROM Meeting m WHERE (m.mentor = :user OR m.learner = :user) AND m.status = 'SCHEDULED' AND m.scheduledDate > :now ORDER BY m.scheduledDate ASC")
    List<Meeting> findUpcomingMeetings(User user, LocalDateTime now);
}