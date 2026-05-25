package com.terangalink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
QUESTION ENTITY
Représente une question publiée par un étudiant.
Exemple :
Comment trouver un logement CROUS ?
Comment renouveler son titre de séjour ?
*/

@Entity
@Table(name = "questions")

@Getter
@Setter
@NoArgsConstructor

public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre question
    private String title;

    // Contenu question
    @Column(columnDefinition = "TEXT")
    private String content;

    // Auteur question
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Date publication
    private LocalDateTime createdAt = LocalDateTime.now();
}