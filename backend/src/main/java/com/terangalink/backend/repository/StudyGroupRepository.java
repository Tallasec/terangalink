package com.terangalink.backend.repository;

import com.terangalink.backend.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
STUDY GROUP REPOSITORY

Gère l'accès aux groupes
de révision.
*/

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>, JpaSpecificationExecutor<StudyGroup> {

    Optional<StudyGroup> findByIdAndDeletedFalse(Long id);

    List<StudyGroup> findByCreatorIdAndDeletedFalseOrderByCreatedAtDesc(Long creatorId);
}
