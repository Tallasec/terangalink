package com.terangalink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
HOUSING ENTITY
Représente une annonce logement.
*/

@Entity
@Table(name = "housing_posts")

@Getter
@Setter
@NoArgsConstructor

public class HousingPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre annonce
    private String title;

    // Description logement
    @Column(columnDefinition = "TEXT")
    private String description;

    // Ville
    private String city;

    // Prix logement
    private Double price;

    // Type logement
    // Studio / Colocation / Chambre
    private String housingType;

    // Auteur annonce
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Date publication
    private LocalDateTime createdAt = LocalDateTime.now();
}