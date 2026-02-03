package com.uniskills.main.model;

import com.uniskills.main.model.Skill;
import com.uniskills.main.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "skill_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt = LocalDateTime.now();
}
