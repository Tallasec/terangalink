package com.terangalink.backend.service;

import com.terangalink.backend.entity.JobPost;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.exception.business.ForumNotFoundException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.JobPostNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.JobPostMapper;
import com.terangalink.backend.repository.JobPostRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateJobPostRequestDTO;
import com.terangalink.backend.requestDTO.UpdateJobPostRequestDTO;
import com.terangalink.backend.responseDTO.JobPostResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.specification.JobPostSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/*
JOB POST SERVICE

Gère les opérations métier
liées aux offres d'emploi.
*/

@Service
@Transactional(readOnly = true)
public class JobPostService {

    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final JobPostMapper jobPostMapper;

    public JobPostService(
            JobPostRepository jobPostRepository,
            UserRepository userRepository,
            JobPostMapper jobPostMapper
    ) {
        this.jobPostRepository = jobPostRepository;
        this.userRepository = userRepository;
        this.jobPostMapper = jobPostMapper;
    }

    // Création d'une offre
    @Transactional
    public JobPostResponseDTO createJobPost(
            CreateJobPostRequestDTO request
    ) {

        UserPrincipal principal = getCurrentPrincipal();

        User owner = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));

        JobPost jobPost = jobPostMapper.toEntity(request);

        jobPost.setOwner(owner);

        jobPost = jobPostRepository.save(jobPost);

        return jobPostMapper.toResponseDto(jobPost);
    }

    // Récupération d'une offre
    public JobPostResponseDTO getJobPostById(Long id) {

        JobPost jobPost = findJobPostByIdOrThrow(id);

        return jobPostMapper.toResponseDto(jobPost);
    }

    // Liste des offres
    public Page<JobPostResponseDTO> getAllJobPosts(Pageable pageable) {

        Specification<JobPost> specification =
                JobPostSpecification.build(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        return jobPostRepository.findAll(specification, pageable)
                .map(jobPostMapper::toResponseDto);
    }

    // Recherche dynamique des offres
    public Page<JobPostResponseDTO> searchJobPosts(
            String title,
            String city,
            String companyName,
            com.terangalink.backend.enums.ContractType contractType,
            BigDecimal salaryMin,
            BigDecimal salaryMax,
            Boolean available,
            Pageable pageable
    ) {

        Specification<JobPost> specification =
                JobPostSpecification.build(
                        title,
                        city,
                        companyName,
                        contractType,
                        salaryMin,
                        salaryMax,
                        available
                );

        return jobPostRepository.findAll(specification, pageable)
                .map(jobPostMapper::toResponseDto);
    }

    // Modification d'une offre
    @Transactional
    public JobPostResponseDTO updateJobPost(
            Long id,
            UpdateJobPostRequestDTO request
    ) {

        JobPost jobPost = findJobPostByIdOrThrow(id);

        jobPostMapper.updateEntity(jobPost, request);

        jobPost = jobPostRepository.save(jobPost);

        return jobPostMapper.toResponseDto(jobPost);
    }

    // Suppression logique d'une offre
    @Transactional
    public void deleteJobPost(Long id) {

        JobPost jobPost = findJobPostByIdOrThrow(id);

        jobPost.setDeleted(true);
        jobPostRepository.save(jobPost);
    }

    // Récupère l'utilisateur actuellement authentifié
    private UserPrincipal getCurrentPrincipal() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {

            throw new InvalidCredentialsException(
                    "Utilisateur non authentifie.");
        }

        return principal;
    }

    // Recherche une offre par son identifiant
    private JobPost findJobPostByIdOrThrow(Long id) {

        return jobPostRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new JobPostNotFoundException(
                                "Offre d'emploi introuvable avec l'id : " + id));
    }
}
