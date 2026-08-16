package com.terangalink.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
FORUM TOPIC VIEW ENTITY

Trace la première consultation
d'un sujet de forum par un utilisateur.
*/

@Entity
@Table(
        name = "forum_topic_views",
        uniqueConstraints = @UniqueConstraint(columnNames = {"forum_topic_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class ForumTopicView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_topic_id", nullable = false)
    private ForumTopic forumTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    @jakarta.persistence.PrePersist
    public void onCreate() {
        if (viewedAt == null) {
            viewedAt = LocalDateTime.now();
        }
    }
}
