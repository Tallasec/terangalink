package com.terangalink.backend.repository;

import com.terangalink.backend.entity.ForumTopicView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumTopicViewRepository extends JpaRepository<ForumTopicView, Long> {

    boolean existsByForumTopicIdAndUserId(Long forumTopicId, Long userId);
}
