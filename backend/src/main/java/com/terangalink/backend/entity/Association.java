package com.terangalink.backend.entity;

import com.terangalink.backend.enums.AssociationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
ASSOCIATION ENTITY

Représente une association
ou un dahira.
*/

@Entity
@Table(name = "associations")

@Getter
@Setter
@NoArgsConstructor

public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de l'association
    @Column(nullable = false)
    private String title;

    // Description
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Ville
    @Column(nullable = false)
    private String city;

    // Adresse
    private String address;

    // Email de contact
    private String contactEmail;

    // Téléphone
    private String phone;

    // Site web
    private String website;

    // Logo
    private String logoUrl;

    // Type d'association
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssociationType associationType;

    // Association active
    @Column(nullable = false)
    private boolean available = true;

    // Suppression logique
    @Column(nullable = false)
    private boolean deleted = false;

    // Créateur
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