package com.terangalink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
ASSOCIATION ENTITY
Représente une association ou dahira.
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

    // Nom association
    private String name;

    // Description
    @Column(columnDefinition = "TEXT")
    private String description;

    // Ville
    private String city;

    // Contact
    private String contact;

    // Responsable
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Date création (pas celui de l'association)
    private LocalDateTime createdAt = LocalDateTime.now();
}