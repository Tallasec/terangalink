package com.terangalink.backend.repository;

import com.terangalink.backend.entity.StudyGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    boolean existsByStudyGroupIdAndUserId(Long studyGroupId, Long userId);

    long countByStudyGroupId(Long studyGroupId);

    Optional<StudyGroupMember> findByStudyGroupIdAndUserId(Long studyGroupId, Long userId);

    List<StudyGroupMember> findByStudyGroupIdOrderByJoinedAtAsc(Long studyGroupId);

    void deleteByStudyGroupIdAndUserId(Long studyGroupId, Long userId);
}
