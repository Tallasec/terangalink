package com.terangalink.backend.entity;

import com.terangalink.backend.enums.MeetingType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
STUDY GROUP ENTITY

Représente un groupe de révision
créé par un étudiant.
*/

@Entity
@Table(name = "study_groups")

@Getter
@Setter
@NoArgsConstructor

public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre du groupe
    @Column(nullable = false)
    private String title;

    // Matière
    @Column(nullable = false)
    private String subject;

    // Description
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Ville
    @Column(nullable = false)
    private String city;

    // Lieu de rendez-vous
    private String location;

    // Type de rencontre
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingType meetingType;

    // Date de la séance
    @Column(nullable = false)
    private LocalDateTime meetingDate;

    // Nombre maximum de participants
    @Column(nullable = false)
    private Integer maxMembers;

    // Groupe ouvert aux inscriptions
    @Column(nullable = false)
    private boolean available = true;

    // Suppression logique
    @Column(nullable = false)
    private boolean deleted = false;

    // Créateur du groupe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // Date de création
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Date de modification
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}