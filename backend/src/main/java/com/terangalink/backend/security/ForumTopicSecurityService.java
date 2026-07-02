package com.terangalink.backend.security;

import com.terangalink.backend.enums.Role;
import com.terangalink.backend.repository.ForumTopicRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
FORUM TOPIC SECURITY SERVICE

Vérifie si un utilisateur peut modifier
ou supprimer un sujet du forum.
*/

@Service("forumTopicSecurityService")
public class ForumTopicSecurityService {

    private final ForumTopicRepository forumTopicRepository;

    public ForumTopicSecurityService(
            ForumTopicRepository forumTopicRepository
    ) {
        this.forumTopicRepository = forumTopicRepository;
    }

    // Vérifie les droits d'accès à un sujet
    public boolean canAccessForumTopic(Long topicId) {

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

        // Un administrateur peut tout gérer
        if (userPrincipal.getRole() == Role.ADMIN) {
            return true;
        }

        if (topicId == null) {
            return false;
        }

        return forumTopicRepository.findById(topicId)
                .map(forumTopic ->
                        forumTopic.getAuthor() != null
                                && forumTopic.getAuthor().getId() != null
                                && forumTopic.getAuthor().getId().equals(userPrincipal.getId())
                                && !forumTopic.isDeleted())
                .orElse(false);
    }
}