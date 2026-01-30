package com.uniskills.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "skills")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;         // Frontend title
    private String description;
    private String category;      // programming, design, business, etc.
    private String proficiency;  // Default 3

    // 🔥🔥🔥 FIX FOR AIVEN DATABASE ERROR 🔥🔥🔥
    // Aiven needs a Primary Key for every table.
    // Adding @OrderColumn creates a Composite Primary Key (skill_id + tag_order).
    @ElementCollection
    @CollectionTable(
            name = "skill_tags",
            joinColumns = @JoinColumn(name = "skill_id")
    )
    @OrderColumn(name = "tag_order") // This is the Magic Fix! 🪄
    @Column(name = "tag_name")
    private List<String> tags;

    private String type;          // TEACH / LEARN

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}