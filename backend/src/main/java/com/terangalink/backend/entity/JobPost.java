package com.terangalink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
JOB ENTITY
Représente une offre d'emploi étudiant.
*/

@Entity
@Table(name = "job_posts")

@Getter
@Setter
@NoArgsConstructor

public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre emploi
    private String title;

    // Description emploi
    @Column(columnDefinition = "TEXT")
    private String description;

    // Ville
    private String city;

    // Type contrat
    // Stage / CDI / CDD / Alternance
    private String contractType;

    // Entreprise
    private String companyName;

    // Auteur publication
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Date publication
    private LocalDateTime createdAt = LocalDateTime.now();
}