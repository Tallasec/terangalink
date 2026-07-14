package com.terangalink.backend.security;

import com.terangalink.backend.entity.Answer;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.repository.AnswerRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
ANSWER SECURITY SERVICE

Vérifie si un utilisateur peut modifier
ou supprimer une réponse du forum.
*/

@Service("answerSecurityService")
public class AnswerSecurityService {

    private final AnswerRepository answerRepository;

    public AnswerSecurityService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    // Vérifie les droits d'accès à une réponse
    public boolean canAccessAnswer(Long id) {

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

        if (userPrincipal.getRole() == Role.ADMIN) {
            return true;
        }

        if (id == null) {
            return false;
        }

        return answerRepository.findById(id)
                .map(answer -> answer.getAuthor() != null
                        && answer.getAuthor().getId() != null
                        && answer.getAuthor().getId().equals(userPrincipal.getId())
                        && !answer.isDeleted())
                .orElse(false);
    }
}
