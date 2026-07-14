package com.terangalink.backend.repository;

import com.terangalink.backend.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
ANSWER REPOSITORY

Gère l'accès aux réponses
du forum.
*/

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByForumTopicIdAndDeletedFalseOrderByCreatedAtAsc(Long forumTopicId);

    Optional<Answer> findByIdAndDeletedFalse(Long id);
}
