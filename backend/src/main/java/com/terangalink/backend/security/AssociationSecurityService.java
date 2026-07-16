package com.terangalink.backend.security;

import com.terangalink.backend.enums.Role;
import com.terangalink.backend.repository.AssociationRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
ASSOCIATION SECURITY SERVICE

Verifie si un utilisateur peut modifier
ou supprimer une association.
*/

@Service("associationSecurityService")
public class AssociationSecurityService {

    private final AssociationRepository associationRepository;

    public AssociationSecurityService(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    // Verifie les droits d'acces a une association
    public boolean canAccessAssociation(Long id) {

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

        return associationRepository.findById(id)
                .map(association -> !association.isDeleted()
                        && (userPrincipal.getRole() == Role.ADMIN
                        || association.getCreator() != null
                        && association.getCreator().getId() != null
                        && association.getCreator().getId().equals(userPrincipal.getId())))
                .orElse(false);
    }
}
