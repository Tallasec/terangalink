package com.terangalink.backend.entity;

import com.terangalink.backend.enums.ForumCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
FORUM TOPIC ENTITY

Représente un sujet publié par un étudiant
sur le forum TerangaLink.
*/

@Entity
@Table(name = "forum_topics")

@Getter
@Setter
@NoArgsConstructor

public class ForumTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre du sujet
    @Column(nullable = false, length = 150)
    private String title;

    // Contenu du sujet
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Catégorie du sujet
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ForumCategory category;

    // Auteur du sujet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Réponses du sujet
    @OneToMany(
            mappedBy = "forumTopic",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Answer> answers = new ArrayList<>();

    // Nombre de vues
    @Column(nullable = false)
    private Long views = 0L;

    // Sujet actif ou supprimé
    @Column(nullable = false)
    private boolean deleted = false;

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