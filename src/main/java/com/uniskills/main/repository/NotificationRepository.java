package com.uniskills.main.repository;

import com.uniskills.main.model.Notification;
import com.uniskills.main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // यूजरचे सर्व नोटिफिकेशन्स (नवीन वरती)
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    // फक्त न वाचलेले (Unread) मोजण्यासाठी (Optional Badge Count)
    long countByRecipientAndIsReadFalse(User recipient);
}