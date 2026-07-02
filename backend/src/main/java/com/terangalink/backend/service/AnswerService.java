package com.terangalink.backend.service;

import com.terangalink.backend.entity.Answer;
import com.terangalink.backend.entity.Question;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.exception.business.AnswerNotFoundException;
import com.terangalink.backend.exception.business.ForumNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.AnswerMapper;
import com.terangalink.backend.repository.AnswerRepository;
import com.terangalink.backend.repository.ForumTopicRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateAnswerRequestDTO;
import com.terangalink.backend.responseDTO.AnswerResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
ANSWER SERVICE

Gère les opérations métier
liées aux réponses.
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
            Long forumTopicId,
            CreateAnswerRequestDTO request
    ) {

        UserPrincipal principal = getCurrentPrincipal();

        User author = userRepository.findById(principal.getId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Utilisateur introuvable avec l'id : "
                                        + principal.getId()));

        Question forumTopic = forumTopicRepository.findById(forumTopicId)
                .orElseThrow(() ->
                        new ForumNotFoundException(
                                "Sujet introuvable avec l'id : "
                                        + forumTopicId));

        Answer answer = answerMapper.toEntity(request);

        answer.setAuthor(author);
        answer.setForumTopic(forumTopic);

        answer = answerRepository.save(answer);

        return answerMapper.toResponseDto(answer);
    }

    // Liste des réponses d'un sujet
    public List<AnswerResponseDTO> getAnswersByForumTopic(Long forumTopicId) {

        forumTopicRepository.findById(forumTopicId)
                .orElseThrow(() ->
                        new ForumNotFoundException(
                                "Sujet introuvable avec l'id : "
                                        + forumTopicId));

        return answerRepository
                .findByForumTopicIdAndDeletedFalseOrderByCreatedAtAsc(forumTopicId)
                .stream()
                .map(answerMapper::toResponseDto)
                .toList();
    }

    // Récupération d'une réponse
    public AnswerResponseDTO getAnswerById(Long id) {

        Answer answer = findAnswerByIdOrThrow(id);

        return answerMapper.toResponseDto(answer);
    }
}