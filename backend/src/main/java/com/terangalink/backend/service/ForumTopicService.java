package com.terangalink.backend.service;

import com.terangalink.backend.entity.ForumTopic;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.ForumCategory;
import com.terangalink.backend.exception.business.ForumNotFoundException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.ForumTopicMapper;
import com.terangalink.backend.repository.ForumTopicRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateForumTopicRequestDTO;
import com.terangalink.backend.requestDTO.UpdateForumTopicRequestDTO;
import com.terangalink.backend.responseDTO.ForumTopicResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.specification.ForumTopicSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
FORUM TOPIC SERVICE

Gère les opérations métier
liées aux sujets du forum.
*/

@Service
@Transactional(readOnly = true)
public class ForumTopicService {

    private final ForumTopicRepository forumTopicRepository;
    private final UserRepository userRepository;
    private final ForumTopicMapper forumTopicMapper;

    public ForumTopicService(
            ForumTopicRepository forumTopicRepository,
            UserRepository userRepository,
            ForumTopicMapper forumTopicMapper
    ) {
        this.forumTopicRepository = forumTopicRepository;
        this.userRepository = userRepository;
        this.forumTopicMapper = forumTopicMapper;
    }

    // Création d'un sujet
    @Transactional
    public ForumTopicResponseDTO createForumTopic(
            CreateForumTopicRequestDTO request
    ) {

        UserPrincipal principal = getCurrentPrincipal();

        User author = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));

        ForumTopic forumTopic = forumTopicMapper.toEntity(request);

        forumTopic.setAuthor(author);

        forumTopic = forumTopicRepository.save(forumTopic);

        return forumTopicMapper.toResponseDto(forumTopic);
    }

    // Récupération d'un sujet
    @Transactional
    public ForumTopicResponseDTO getForumTopicById(Long id) {

        ForumTopic forumTopic = findForumTopicByIdOrThrow(id);

        if (forumTopic.isDeleted()) {
            throw new ForumNotFoundException(
                    "Sujet introuvable avec l'id : " + id);
        }

        forumTopic.setViews(forumTopic.getViews() + 1);

        forumTopicRepository.save(forumTopic);

        return forumTopicMapper.toResponseDto(forumTopic);
    }

    // Liste des sujets
    public Page<ForumTopicResponseDTO> getAllForumTopics(
            Pageable pageable
    ) {

        Specification<ForumTopic> specification =
                ForumTopicSpecification.build(
                        null,
                        null
                );

        return forumTopicRepository.findAll(specification, pageable)
                .map(forumTopicMapper::toResponseDto);
    }

    // Recherche dynamique des sujets
    public Page<ForumTopicResponseDTO> searchForumTopics(
            String title,
            ForumCategory category,
            Pageable pageable
    ) {

        Specification<ForumTopic> specification =
                ForumTopicSpecification.build(
                        title,
                        category
                );

        return forumTopicRepository.findAll(specification, pageable)
                .map(forumTopicMapper::toResponseDto);
    }

    // Modification d'un sujet
    @Transactional
    public ForumTopicResponseDTO updateForumTopic(
            Long id,
            UpdateForumTopicRequestDTO request
    ) {

        ForumTopic forumTopic = findForumTopicByIdOrThrow(id);

        forumTopicMapper.updateEntity(forumTopic, request);

        forumTopic = forumTopicRepository.save(forumTopic);

        return forumTopicMapper.toResponseDto(forumTopic);
    }

    // Suppression logique d'un sujet
    @Transactional
    public void deleteForumTopic(Long id) {

        ForumTopic forumTopic = findForumTopicByIdOrThrow(id);

        forumTopic.setDeleted(true);

        forumTopicRepository.save(forumTopic);
    }

    // Récupère l'utilisateur actuellement authentifié
    private UserPrincipal getCurrentPrincipal() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {

            throw new InvalidCredentialsException(
                    "Utilisateur non authentifié.");
        }

        return principal;
    }

    // Recherche un sujet par son identifiant
    private ForumTopic findForumTopicByIdOrThrow(Long id) {

        return forumTopicRepository.findById(id)
                .orElseThrow(() ->
                        new ForumNotFoundException(
                                "Sujet introuvable avec l'id : " + id));
    }

}
