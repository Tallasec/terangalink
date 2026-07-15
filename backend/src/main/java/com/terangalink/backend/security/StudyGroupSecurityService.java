package com.terangalink.backend.security;

import com.terangalink.backend.enums.Role;
import com.terangalink.backend.repository.StudyGroupRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
STUDY GROUP SECURITY SERVICE

Vérifie si un utilisateur peut modifier
ou supprimer un groupe de révision.
*/

@Service("studyGroupSecurityService")
public class StudyGroupSecurityService {

    private final StudyGroupRepository studyGroupRepository;

    public StudyGroupSecurityService(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    // Vérifie les droits d'accès à un groupe
    public boolean canAccessStudyGroup(Long id) {

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

        return studyGroupRepository.findById(id)
                .map(studyGroup -> !studyGroup.isDeleted()
                        && (userPrincipal.getRole() == Role.ADMIN
                        || studyGroup.getCreator() != null
                        && studyGroup.getCreator().getId() != null
                        && studyGroup.getCreator().getId().equals(userPrincipal.getId())))
                .orElse(false);
    }
}
