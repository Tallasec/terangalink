package com.terangalink.backend.service;

import com.terangalink.backend.entity.Answer;
import com.terangalink.backend.entity.ForumTopic;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.exception.business.AnswerNotFoundException;
import com.terangalink.backend.exception.business.ForumNotFoundException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.AnswerMapper;
import com.terangalink.backend.repository.AnswerRepository;
import com.terangalink.backend.repository.ForumTopicRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateAnswerRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAnswerRequestDTO;
import com.terangalink.backend.responseDTO.AnswerResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
ANSWER SERVICE

Gère les opérations métier
liées aux réponses du forum.
*/

@Service
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final ForumTopicRepository forumTopicRepository;
    private final UserRepository userRepository;
    private final AnswerMapper answerMapper;

    public AnswerService(
            AnswerRepository answerRepository,
            ForumTopicRepository forumTopicRepository,
            UserRepository userRepository,
            AnswerMapper answerMapper
    ) {
        this.answerRepository = answerRepository;
        this.forumTopicRepository = forumTopicRepository;
        this.userRepository = userRepository;
        this.answerMapper = answerMapper;
    }

    // Création d'une réponse
    @Transactional
    public AnswerResponseDTO createAnswer(
            Long topicId,
            CreateAnswerRequestDTO request
    ) {

        UserPrincipal principal = getCurrentPrincipal();

        User author = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));

        ForumTopic forumTopic = findForumTopicByIdOrThrow(topicId);

        Answer answer = answerMapper.toEntity(request);
        answer.setAuthor(author);
        answer.setForumTopic(forumTopic);

        answer = answerRepository.save(answer);

        return answerMapper.toResponseDto(answer);
    }

    // Récupération d'une réponse
    public AnswerResponseDTO getAnswerById(Long id) {

        Answer answer = findAnswerByIdOrThrow(id);

        if (answer.isDeleted()) {
            throw new AnswerNotFoundException(
                    "Réponse introuvable avec l'id : " + id);
        }

        return answerMapper.toResponseDto(answer);
    }

    // Liste des réponses d'un sujet
    public List<AnswerResponseDTO> getAnswersByForumTopic(Long topicId) {

        ForumTopic forumTopic = findForumTopicByIdOrThrow(topicId);

        if (forumTopic.isDeleted()) {
            throw new ForumNotFoundException(
                    "Sujet introuvable avec l'id : " + topicId);
        }

        return answerRepository
                .findByForumTopicIdAndDeletedFalseOrderByCreatedAtAsc(topicId)
                .stream()
                .map(answerMapper::toResponseDto)
                .toList();
    }

    // Modification d'une réponse
    @Transactional
    public AnswerResponseDTO updateAnswer(
            Long id,
            UpdateAnswerRequestDTO request
    ) {

        Answer answer = findAnswerByIdOrThrow(id);

        if (answer.isDeleted()) {
            throw new AnswerNotFoundException(
                    "Réponse introuvable avec l'id : " + id);
        }

        answerMapper.updateEntity(answer, request);

        answer = answerRepository.save(answer);

        return answerMapper.toResponseDto(answer);
    }

    // Suppression logique d'une réponse
    @Transactional
    public void deleteAnswer(Long id) {

        Answer answer = findAnswerByIdOrThrow(id);

        if (answer.isDeleted()) {
            throw new AnswerNotFoundException(
                    "Réponse introuvable avec l'id : " + id);
        }

        answer.setDeleted(true);
        answerRepository.save(answer);
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
                .filter(forumTopic -> !forumTopic.isDeleted())
                .orElseThrow(() ->
                        new ForumNotFoundException(
                                "Sujet introuvable avec l'id : " + id));
    }

    // Recherche une réponse par son identifiant
    private Answer findAnswerByIdOrThrow(Long id) {

        return answerRepository.findById(id)
                .orElseThrow(() ->
                        new AnswerNotFoundException(
                                "Réponse introuvable avec l'id : " + id));
    }
}
