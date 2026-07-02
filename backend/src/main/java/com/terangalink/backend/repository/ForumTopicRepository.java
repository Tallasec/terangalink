package com.terangalink.backend.repository;

import com.terangalink.backend.entity.ForumTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/*
FORUM REPOSITORY

Gère l'accès aux sujets
du forum.
*/

@Repository
public interface ForumTopicRepository
        extends JpaRepository<ForumTopic, Long>,
        JpaSpecificationExecutor<ForumTopic> {
}