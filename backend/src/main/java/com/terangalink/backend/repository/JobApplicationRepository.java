package com.terangalink.backend.repository;

import com.terangalink.backend.entity.JobApplication;
import com.terangalink.backend.enums.JobApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Optional<JobApplication> findByJobPostIdAndApplicantIdAndStatus(
            Long jobPostId,
            Long applicantId,
            JobApplicationStatus status);

    List<JobApplication> findByApplicantIdOrderByCreatedAtDesc(Long applicantId);

    List<JobApplication> findByJobPostIdOrderByCreatedAtDesc(Long jobPostId);
}
