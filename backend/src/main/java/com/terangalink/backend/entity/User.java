package com.terangalink.backend.entity;

import com.terangalink.backend.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Locale;


/*
USER ENTITY
Représente un utilisateur de la plateforme TerangaLink.
Chaque utilisateur peut :
poser des questions
publier des annonces logement
rejoindre des groupes de révision
publier des offres d'emploi
*/

@Entity
@Table(name = "users")

@Getter
@Setter
@NoArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Prénom utilisateur
    @Column(nullable = false, length = 100)
    private String firstName;

    // Nom utilisateur
    @Column(nullable = false, length = 100)
    private String lastName;

    // Email unique
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    // Mot de passe
    @Column(nullable = false, length = 255)
    private String password;

    // Verification email
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean emailVerified = false;

    // Université en France
    @Column(nullable = false, length = 150)
    private String university;

    // Domaine d'étude
    @Column(nullable = false, length = 150)
    private String fieldOfStudy;

    // Ville en France
    @Column(nullable = false, length = 120)
    private String city;

    // USER / ADMIN
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // Date création compte
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        normalizeEmail();
    }

    @PreUpdate
    public void onUpdate() {
        normalizeEmail();
    }

    private void normalizeEmail() {
        if (email != null) {
            email = email.trim().toLowerCase(Locale.ROOT);
        }
    }
}
