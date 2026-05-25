package com.terangalink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
ANSWER ENTITY
Représente une réponse à une question.
*/

@Entity
@Table(name = "answers")

@Getter
@Setter
@NoArgsConstructor

public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Contenu réponse
    @Column(columnDefinition = "TEXT")
    private String content;

    // Question liée
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    // Auteur réponse
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Date publication
    private LocalDateTime createdAt = LocalDateTime.now();
}