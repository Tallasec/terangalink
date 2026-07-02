package com.terangalink.backend.repository;

import com.terangalink.backend.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
ANSWER REPOSITORY

Gère l'accès aux réponses
du forum.
*/

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // Liste des réponses d'un sujet
    List<Answer> findByForumTopicIdAndDeletedFalseOrderByCreatedAtAsc(
            Long forumTopicId
    );

    // Nombre de réponses d'un sujet
    long countByForumTopicIdAndDeletedFalse(
            Long forumTopicId
    );

    // Vérifie si une réponse existe
    boolean existsByIdAndDeletedFalse(
            Long id
    );

}