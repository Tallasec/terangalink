package com.terangalink.backend.entity;

import com.terangalink.backend.enums.ContractType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
JOB POST ENTITY

Représente une offre d'emploi
publiée par un utilisateur.
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

    // Titre de l'offre
    @Column(nullable = false)
    private String title;

    // Description de l'offre
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Nom de l'entreprise
    @Column(nullable = false)
    private String companyName;

    // Ville
    @Column(nullable = false)
    private String city;

    // Adresse
    private String address;

    // Type de contrat
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType contractType;

    // Salaire
    private BigDecimal salary;

    // Offre disponible
    @Column(nullable = false)
    private boolean available = true;

    // Offre supprimée
    @Column(nullable = false)
    private boolean deleted = false;

    // Auteur de l'offre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

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