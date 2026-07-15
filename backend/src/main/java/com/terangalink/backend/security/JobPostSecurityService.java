package com.terangalink.backend.security;

import com.terangalink.backend.enums.Role;
import com.terangalink.backend.repository.JobPostRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
JOB POST SECURITY SERVICE

Vérifie si un utilisateur peut modifier
ou supprimer une offre d'emploi.
*/

@Service("jobPostSecurityService")
public class JobPostSecurityService {

    private final JobPostRepository jobPostRepository;

    public JobPostSecurityService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }

    // Vérifie les droits d'accès à une offre
    public boolean canAccessJobPost(Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            return false;
        }

        if (id == null) {
            return false;
        }

        return jobPostRepository.findById(id)
                .map(jobPost -> !jobPost.isDeleted()
                        && (userPrincipal.getRole() == Role.ADMIN
                        || jobPost.getOwner() != null
                        && jobPost.getOwner().getId() != null
                        && jobPost.getOwner().getId().equals(userPrincipal.getId())))
                .orElse(false);
    }
}
