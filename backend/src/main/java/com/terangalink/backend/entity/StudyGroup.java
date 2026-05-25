package com.terangalink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
STUDY GROUP ENTITY
Représente un groupe de révision.
Exemple :
Groupe Java
Groupe Mathématiques
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

    // Nom du groupe
    private String name;

    // Matière concernée
    private String subject;

    // Description groupe
    @Column(columnDefinition = "TEXT")
    private String description;

    // Ville
    private String city;

    // Créateur groupe
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    // Date création
    private LocalDateTime createdAt = LocalDateTime.now();
}