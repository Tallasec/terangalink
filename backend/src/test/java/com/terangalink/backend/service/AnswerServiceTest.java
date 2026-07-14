package com.terangalink.backend.service;

import com.terangalink.backend.entity.Answer;
import com.terangalink.backend.entity.ForumTopic;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.ForumCategory;
import com.terangalink.backend.exception.business.AnswerNotFoundException;
import com.terangalink.backend.exception.business.ForumNotFoundException;
import com.terangalink.backend.mapper.AnswerMapper;
import com.terangalink.backend.repository.AnswerRepository;
import com.terangalink.backend.repository.ForumTopicRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateAnswerRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAnswerRequestDTO;
import com.terangalink.backend.responseDTO.AnswerResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.support.AuthTestFixtures;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private ForumTopicRepository forumTopicRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnswerMapper answerMapper;

    @InjectMocks
    private AnswerService answerService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createAnswer_shouldCreateAnswer() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        setAuthentication(principal);

        User author = UserTestFixtures.sampleUser(1L);
        ForumTopic forumTopic = sampleForumTopic(10L, author);
        CreateAnswerRequestDTO request = createRequest();
        Answer answer = sampleAnswer(20L, forumTopic, author);
        AnswerResponseDTO response = sampleResponse(20L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(author));
        when(forumTopicRepository.findById(10L))
                .thenReturn(Optional.of(forumTopic));
        when(answerMapper.toEntity(request))
                .thenReturn(new Answer());
        when(answerRepository.save(any(Answer.class)))
                .thenReturn(answer);
        when(answerMapper.toResponseDto(answer))
                .thenReturn(response);

        AnswerResponseDTO result =
                answerService.createAnswer(10L, request);

        assertThat(result.getId()).isEqualTo(20L);
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void getAnswerById_shouldReturnAnswer() {

        User author = UserTestFixtures.sampleUser(1L);
        ForumTopic forumTopic = sampleForumTopic(10L, author);
        Answer answer = sampleAnswer(20L, forumTopic, author);
        AnswerResponseDTO response = sampleResponse(20L);

        when(answerRepository.findById(20L))
                .thenReturn(Optional.of(answer));
        when(answerMapper.toResponseDto(answer))
                .thenReturn(response);

        AnswerResponseDTO result =
                answerService.getAnswerById(20L);

        assertThat(result.getId()).isEqualTo(20L);
    }

    @Test
    void getAnswerById_shouldThrowExceptionWhenMissing() {

        when(answerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                answerService.getAnswerById(99L))
                .isInstanceOf(AnswerNotFoundException.class);
    }

    @Test
    void getAnswersByForumTopic_shouldReturnList() {

        User author = UserTestFixtures.sampleUser(1L);
        ForumTopic forumTopic = sampleForumTopic(10L, author);
        Answer answer = sampleAnswer(20L, forumTopic, author);
        AnswerResponseDTO response = sampleResponse(20L);

        when(forumTopicRepository.findById(10L))
                .thenReturn(Optional.of(forumTopic));
        when(answerRepository.findByForumTopicIdAndDeletedFalseOrderByCreatedAtAsc(10L))
                .thenReturn(List.of(answer));
        when(answerMapper.toResponseDto(answer))
                .thenReturn(response);

        List<AnswerResponseDTO> result =
                answerService.getAnswersByForumTopic(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(20L);
    }

    @Test
    void getAnswersByForumTopic_shouldThrowWhenTopicDeleted() {

        User author = UserTestFixtures.sampleUser(1L);
        ForumTopic forumTopic = sampleForumTopic(10L, author);
        forumTopic.setDeleted(true);

        when(forumTopicRepository.findById(10L))
                .thenReturn(Optional.of(forumTopic));

        assertThatThrownBy(() ->
                answerService.getAnswersByForumTopic(10L))
                .isInstanceOf(ForumNotFoundException.class);
    }

    @Test
    void updateAnswer_shouldUpdateAnswer() {

        User author = UserTestFixtures.sampleUser(1L);
        ForumTopic forumTopic = sampleForumTopic(10L, author);
        Answer answer = sampleAnswer(20L, forumTopic, author);
        UpdateAnswerRequestDTO request = updateRequest();
        AnswerResponseDTO response = sampleResponse(20L);

        when(answerRepository.findById(20L))
                .thenReturn(Optional.of(answer));
        when(answerRepository.save(answer))
                .thenReturn(answer);
        when(answerMapper.toResponseDto(answer))
                .thenReturn(response);

        AnswerResponseDTO result =
                answerService.updateAnswer(20L, request);

        assertThat(result.getId()).isEqualTo(20L);
    }

    @Test
    void deleteAnswer_shouldSoftDeleteAnswer() {

        User author = UserTestFixtures.sampleUser(1L);
        ForumTopic forumTopic = sampleForumTopic(10L, author);
        Answer answer = sampleAnswer(20L, forumTopic, author);

        when(answerRepository.findById(20L))
                .thenReturn(Optional.of(answer));
        when(answerRepository.save(answer))
                .thenReturn(answer);

        answerService.deleteAnswer(20L);

        assertThat(answer.isDeleted()).isTrue();
        verify(answerRepository).save(answer);
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private CreateAnswerRequestDTO createRequest() {

        CreateAnswerRequestDTO dto =
                new CreateAnswerRequestDTO();

        dto.setContent("Merci pour cette précision.");

        return dto;
    }

    private UpdateAnswerRequestDTO updateRequest() {

        UpdateAnswerRequestDTO dto =
                new UpdateAnswerRequestDTO();

        dto.setContent("Contenu modifié");

        return dto;
    }

    private ForumTopic sampleForumTopic(
            Long id,
            User author
    ) {

        ForumTopic forumTopic = new ForumTopic();

        forumTopic.setId(id);
        forumTopic.setTitle("Comment trouver un logement ?");
        forumTopic.setContent("Je cherche un studio.");
        forumTopic.setCategory(ForumCategory.LOGEMENT);
        forumTopic.setAuthor(author);
        forumTopic.setViews(0L);
        forumTopic.setDeleted(false);
        forumTopic.setCreatedAt(LocalDateTime.now());
        forumTopic.setUpdatedAt(LocalDateTime.now());

        return forumTopic;
    }

    private Answer sampleAnswer(
            Long id,
            ForumTopic forumTopic,
            User author
    ) {

        Answer answer = new Answer();

        answer.setId(id);
        answer.setContent("Merci pour cette précision.");
        answer.setForumTopic(forumTopic);
        answer.setAuthor(author);
        answer.setDeleted(false);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());

        return answer;
    }

    private AnswerResponseDTO sampleResponse(Long id) {

        AnswerResponseDTO dto =
                new AnswerResponseDTO();

        dto.setId(id);
        dto.setContent("Merci pour cette précision.");
        dto.setForumTopicId(10L);
        dto.setAuthorId(1L);
        dto.setAuthorFirstName("Alice");
        dto.setAuthorLastName("Dupont");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        return dto;
    }
}
