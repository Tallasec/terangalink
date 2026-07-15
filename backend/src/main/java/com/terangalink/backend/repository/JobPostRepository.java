package com.terangalink.backend.repository;

import com.terangalink.backend.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
JOB POST REPOSITORY

Gère l'accès aux offres
d'emploi.
*/

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {

    Optional<JobPost> findByIdAndDeletedFalse(Long id);

    Page<JobPost> findByDeletedFalse(Pageable pageable);

    List<JobPost> findByOwnerIdAndDeletedFalseOrderByCreatedAtDesc(Long ownerId);
}
