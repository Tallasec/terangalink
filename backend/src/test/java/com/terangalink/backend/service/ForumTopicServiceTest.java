package com.terangalink.backend.service;

import com.terangalink.backend.entity.ForumTopic;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.ForumCategory;
import com.terangalink.backend.exception.business.ForumNotFoundException;
import com.terangalink.backend.mapper.ForumTopicMapper;
import com.terangalink.backend.repository.ForumTopicRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateForumTopicRequestDTO;
import com.terangalink.backend.requestDTO.UpdateForumTopicRequestDTO;
import com.terangalink.backend.responseDTO.ForumTopicResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.support.AuthTestFixtures;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumTopicServiceTest {

    @Mock
    private ForumTopicRepository forumTopicRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ForumTopicMapper forumTopicMapper;

    @InjectMocks
    private ForumTopicService forumTopicService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createForumTopic_shouldCreateTopic() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        setAuthentication(principal);

        User author = UserTestFixtures.sampleUser(1L);

        CreateForumTopicRequestDTO request = createRequest();

        ForumTopic forumTopic = sampleForumTopic(1L, author);

        ForumTopicResponseDTO response = sampleResponse(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(forumTopicMapper.toEntity(request))
                .thenReturn(forumTopic);

        when(forumTopicRepository.save(any(ForumTopic.class)))
                .thenReturn(forumTopic);

        when(forumTopicMapper.toResponseDto(forumTopic))
                .thenReturn(response);

        ForumTopicResponseDTO result =
                forumTopicService.createForumTopic(request);

        assertThat(result.getId()).isEqualTo(1L);

        verify(forumTopicRepository).save(any(ForumTopic.class));
    }

    @Test
    void getForumTopicById_shouldReturnTopic() {

        User author = UserTestFixtures.sampleUser(1L);

        ForumTopic forumTopic =
                sampleForumTopic(1L, author);

        ForumTopicResponseDTO response =
                sampleResponse(1L);

        when(forumTopicRepository.findById(1L))
                .thenReturn(Optional.of(forumTopic));

        when(forumTopicRepository.save(any()))
                .thenReturn(forumTopic);

        when(forumTopicMapper.toResponseDto(forumTopic))
                .thenReturn(response);

        ForumTopicResponseDTO result =
                forumTopicService.getForumTopicById(1L);

        assertThat(result.getId()).isEqualTo(1L);

        verify(forumTopicRepository).save(any());
    }

    @Test
    void getForumTopicById_shouldThrowException() {

        when(forumTopicRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                forumTopicService.getForumTopicById(99L))
                .isInstanceOf(ForumNotFoundException.class);
    }

    @Test
    void getAllForumTopics_shouldReturnPage() {

        User author = UserTestFixtures.sampleUser(1L);

        ForumTopic forumTopic =
                sampleForumTopic(1L, author);

        ForumTopicResponseDTO response =
                sampleResponse(1L);

        PageRequest pageable =
                PageRequest.of(0,20);

        when(forumTopicRepository.findAll(
                any(Specification.class),
                eq(pageable)))
                .thenReturn(new PageImpl<>(
                        List.of(forumTopic),
                        pageable,
                        1));

        when(forumTopicMapper.toResponseDto(forumTopic))
                .thenReturn(response);

        Page<ForumTopicResponseDTO> result =
                forumTopicService.getAllForumTopics(pageable);

        assertThat(result.getContent())
                .hasSize(1);
    }
    @Test
    void searchForumTopics_shouldReturnPage() {

        User author = UserTestFixtures.sampleUser(1L);

        ForumTopic forumTopic = sampleForumTopic(1L, author);

        ForumTopicResponseDTO response = sampleResponse(1L);

        PageRequest pageable = PageRequest.of(0, 20);

        when(forumTopicRepository.findAll(
                any(Specification.class),
                eq(pageable)))
                .thenReturn(new PageImpl<>(
                        List.of(forumTopic),
                        pageable,
                        1));

        when(forumTopicMapper.toResponseDto(forumTopic))
                .thenReturn(response);

        Page<ForumTopicResponseDTO> result =
                forumTopicService.searchForumTopics(
                        "logement",
                        ForumCategory.LOGEMENT,
                        pageable
                );

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void updateForumTopic_shouldUpdateTopic() {

        User author = UserTestFixtures.sampleUser(1L);

        ForumTopic forumTopic = sampleForumTopic(1L, author);

        UpdateForumTopicRequestDTO request = updateRequest();

        ForumTopicResponseDTO response = sampleResponse(1L);

        when(forumTopicRepository.findById(1L))
                .thenReturn(Optional.of(forumTopic));

        doNothing().when(forumTopicMapper)
                .updateEntity(forumTopic, request);

        when(forumTopicRepository.save(forumTopic))
                .thenReturn(forumTopic);

        when(forumTopicMapper.toResponseDto(forumTopic))
                .thenReturn(response);

        ForumTopicResponseDTO result =
                forumTopicService.updateForumTopic(1L, request);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deleteForumTopic_shouldSoftDeleteTopic() {

        User author = UserTestFixtures.sampleUser(1L);

        ForumTopic forumTopic = sampleForumTopic(1L, author);

        when(forumTopicRepository.findById(1L))
                .thenReturn(Optional.of(forumTopic));

        when(forumTopicRepository.save(any()))
                .thenReturn(forumTopic);

        forumTopicService.deleteForumTopic(1L);

        assertThat(forumTopic.isDeleted()).isTrue();

        verify(forumTopicRepository).save(forumTopic);
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private CreateForumTopicRequestDTO createRequest() {

        CreateForumTopicRequestDTO dto =
                new CreateForumTopicRequestDTO();

        dto.setTitle("Comment trouver un logement ?");
        dto.setContent("Je cherche un studio.");
        dto.setCategory(ForumCategory.LOGEMENT);

        return dto;
    }

    private UpdateForumTopicRequestDTO updateRequest() {

        UpdateForumTopicRequestDTO dto =
                new UpdateForumTopicRequestDTO();

        dto.setTitle("Titre modifié");
        dto.setContent("Contenu modifié");
        dto.setCategory(ForumCategory.LOGEMENT);

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

    private ForumTopicResponseDTO sampleResponse(Long id) {

        ForumTopicResponseDTO dto =
                new ForumTopicResponseDTO();

        dto.setId(id);
        dto.setTitle("Comment trouver un logement ?");
        dto.setContent("Je cherche un studio.");
        dto.setCategory(ForumCategory.LOGEMENT);
        dto.setAuthorId(1L);
        dto.setAuthorFirstName("Talla");
        dto.setAuthorLastName("Seck");
        dto.setViews(0L);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        return dto;
    }

}