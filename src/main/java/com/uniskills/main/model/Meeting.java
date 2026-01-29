package com.uniskills.main.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥🔥 NEW: Link Meeting to Request (Foreign Key)
    // एका Request साठी एक किंवा अनेक मीटिंग्स असू शकतात, पण सोपे ठेवण्यासाठी OneToOne ठेवू
    @OneToOne
    @JoinColumn(name = "request_id")
    private SkillExchangeRequest request;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ManyToOne
    @JoinColumn(name = "learner_id")
    private User learner;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private String title;
    private String description;

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    private int duration; // in minutes

    private String status; // SCHEDULED, COMPLETED, CANCELLED

    private Integer rating;
    @Column(length = 500)
    private String feedback;
}