package com.terangalink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


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
    private String firstName;

    // Nom utilisateur
    private String lastName;

    // Email unique
    @Column(unique = true, nullable = false)
    private String email;

    // Mot de passe
    private String password;

    // Université en France
    private String university;

    // Domaine d'étude
    private String fieldOfStudy;

    // Ville en France
    private String city;

    // USER / ADMIN
    private String role;

    // Date création compte
    private LocalDateTime createdAt = LocalDateTime.now();
}