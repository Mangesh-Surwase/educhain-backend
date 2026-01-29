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

    // Rating Calculation साठी
    List<Meeting> findByMentor_IdAndStatus(Long mentorId, String status);

    // 🔥🔥🔥 NEW: For Dashboard Stats (Count Total Completed Sessions)
    // जिथे यूजर Mentor किंवा Learner आहे आणि स्टेटस COMPLETED आहे
    long countByMentorOrLearnerAndStatus(User mentor, User learner, String status);

    // 🔥🔥🔥 NEW: For Next Upcoming Meeting
    // ही Query अशा मिटिंग शोधेल ज्या:
    // 1. यूजर Mentor किंवा Learner आहे.
    // 2. स्टेटस 'SCHEDULED' आहे.
    // 3. तारीख आजच्या नंतरची आहे (Future).
    // 4. सर्वात जवळची तारीख पहिली येईल (ASC Order).
    @Query("SELECT m FROM Meeting m WHERE (m.mentor = :user OR m.learner = :user) AND m.status = 'SCHEDULED' AND m.scheduledDate > :now ORDER BY m.scheduledDate ASC")
    List<Meeting> findUpcomingMeetings(User user, LocalDateTime now);
}