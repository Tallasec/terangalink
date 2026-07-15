package com.terangalink.backend.service;

import com.terangalink.backend.entity.StudyGroup;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.MeetingType;
import com.terangalink.backend.exception.business.StudyGroupNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.StudyGroupMapper;
import com.terangalink.backend.repository.StudyGroupRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateStudyGroupRequestDTO;
import com.terangalink.backend.requestDTO.UpdateStudyGroupRequestDTO;
import com.terangalink.backend.responseDTO.StudyGroupResponseDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyGroupServiceTest {

    @Mock
    private StudyGroupRepository studyGroupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudyGroupMapper studyGroupMapper;

    @InjectMocks
    private StudyGroupService studyGroupService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createStudyGroup_shouldCreateGroup() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        setAuthentication(principal);

        User creator = UserTestFixtures.sampleUser(1L);
        CreateStudyGroupRequestDTO request = createRequest();
        StudyGroup studyGroup = sampleStudyGroup(null, creator);
        StudyGroup savedStudyGroup = sampleStudyGroup(10L, creator);
        StudyGroupResponseDTO response = sampleResponse(10L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(creator));
        when(studyGroupMapper.toEntity(request))
                .thenReturn(studyGroup);
        when(studyGroupRepository.save(studyGroup))
                .thenReturn(savedStudyGroup);
        when(studyGroupMapper.toResponseDto(savedStudyGroup))
                .thenReturn(response);

        StudyGroupResponseDTO result =
                studyGroupService.createStudyGroup(request);

        assertThat(result).isEqualTo(response);
        assertThat(studyGroup.getCreator()).isEqualTo(creator);
        verify(studyGroupRepository).save(studyGroup);
    }

    @Test
    void createStudyGroup_shouldThrowWhenCurrentUserDoesNotExist() {

        CreateStudyGroupRequestDTO request = createRequest();
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        setAuthentication(principal);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                studyGroupService.createStudyGroup(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Utilisateur introuvable avec l'id : 1");

        verify(studyGroupRepository, never()).save(any(StudyGroup.class));
    }

    @Test
    void getStudyGroupById_shouldReturnGroup() {

        User creator = UserTestFixtures.sampleUser(1L);
        StudyGroup studyGroup = sampleStudyGroup(10L, creator);
        StudyGroupResponseDTO response = sampleResponse(10L);

        when(studyGroupRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(studyGroup));
        when(studyGroupMapper.toResponseDto(studyGroup))
                .thenReturn(response);

        assertThat(studyGroupService.getStudyGroupById(10L)).isEqualTo(response);
    }

    @Test
    void getStudyGroupById_shouldThrowWhenGroupNotFound() {

        when(studyGroupRepository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                studyGroupService.getStudyGroupById(99L))
                .isInstanceOf(StudyGroupNotFoundException.class)
                .hasMessage("Groupe introuvable avec l'id : 99");
    }

    @Test
    void getAllStudyGroups_shouldReturnMappedPage() {

        PageRequest pageable = PageRequest.of(0, 20);
        User creator = UserTestFixtures.sampleUser(1L);
        StudyGroup studyGroup = sampleStudyGroup(10L, creator);
        StudyGroupResponseDTO response = sampleResponse(10L);
        Page<StudyGroup> page = new PageImpl<>(List.of(studyGroup), pageable, 1);

        when(studyGroupRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(studyGroupMapper.toResponseDto(studyGroup))
                .thenReturn(response);

        Page<StudyGroupResponseDTO> result = studyGroupService.getAllStudyGroups(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchStudyGroups_shouldReturnMappedPage() {

        PageRequest pageable = PageRequest.of(0, 20);
        User creator = UserTestFixtures.sampleUser(1L);
        StudyGroup studyGroup = sampleStudyGroup(10L, creator);
        StudyGroupResponseDTO response = sampleResponse(10L);
        Page<StudyGroup> page = new PageImpl<>(List.of(studyGroup), pageable, 1);

        when(studyGroupRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(studyGroupMapper.toResponseDto(studyGroup))
                .thenReturn(response);

        Page<StudyGroupResponseDTO> result = studyGroupService.searchStudyGroups(
                "Révisions Java",
                "Mathématiques",
                "Dakar",
                MeetingType.ONLINE,
                true,
                LocalDateTime.of(2026, 8, 1, 10, 0),
                pageable
        );

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void updateStudyGroup_shouldPatchGroup() {

        User creator = UserTestFixtures.sampleUser(1L);
        StudyGroup studyGroup = sampleStudyGroup(10L, creator);
        UpdateStudyGroupRequestDTO request = updateRequest();
        StudyGroupResponseDTO response = sampleResponse(10L);

        when(studyGroupRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(studyGroup));
        when(studyGroupRepository.save(studyGroup))
                .thenReturn(studyGroup);
        when(studyGroupMapper.toResponseDto(studyGroup))
                .thenReturn(response);

        StudyGroupResponseDTO result = studyGroupService.updateStudyGroup(10L, request);

        assertThat(result).isEqualTo(response);
        verify(studyGroupMapper).updateEntity(studyGroup, request);
    }

    @Test
    void deleteStudyGroup_shouldSoftDeleteGroup() {

        User creator = UserTestFixtures.sampleUser(1L);
        StudyGroup studyGroup = sampleStudyGroup(10L, creator);

        when(studyGroupRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(studyGroup));
        when(studyGroupRepository.save(studyGroup))
                .thenReturn(studyGroup);

        studyGroupService.deleteStudyGroup(10L);

        assertThat(studyGroup.isDeleted()).isTrue();
        verify(studyGroupRepository).save(studyGroup);
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private CreateStudyGroupRequestDTO createRequest() {

        CreateStudyGroupRequestDTO dto =
                new CreateStudyGroupRequestDTO();

        dto.setTitle("Révisions Java");
        dto.setSubject("Mathématiques");
        dto.setDescription("Séance de révision pour les examens.");
        dto.setCity("Dakar");
        dto.setLocation("Bibliothèque");
        dto.setMeetingType(MeetingType.ONLINE);
        dto.setMeetingDate(LocalDateTime.of(2026, 8, 1, 10, 0));
        dto.setMaxMembers(8);
        dto.setAvailable(true);

        return dto;
    }

    private UpdateStudyGroupRequestDTO updateRequest() {

        UpdateStudyGroupRequestDTO dto =
                new UpdateStudyGroupRequestDTO();

        dto.setTitle("Révisions Java Avancées");
        dto.setMaxMembers(10);

        return dto;
    }

    private StudyGroup sampleStudyGroup(
            Long id,
            User creator
    ) {

        StudyGroup studyGroup = new StudyGroup();

        studyGroup.setId(id);
        studyGroup.setTitle("Révisions Java");
        studyGroup.setSubject("Mathématiques");
        studyGroup.setDescription("Séance de révision pour les examens.");
        studyGroup.setCity("Dakar");
        studyGroup.setLocation("Bibliothèque");
        studyGroup.setMeetingType(MeetingType.ONLINE);
        studyGroup.setMeetingDate(LocalDateTime.of(2026, 8, 1, 10, 0));
        studyGroup.setMaxMembers(8);
        studyGroup.setAvailable(true);
        studyGroup.setDeleted(false);
        studyGroup.setCreator(creator);
        studyGroup.setCreatedAt(LocalDateTime.now());
        studyGroup.setUpdatedAt(LocalDateTime.now());

        return studyGroup;
    }

    private StudyGroupResponseDTO sampleResponse(Long id) {

        StudyGroupResponseDTO dto =
                new StudyGroupResponseDTO();

        dto.setId(id);
        dto.setTitle("Révisions Java");
        dto.setSubject("Mathématiques");
        dto.setDescription("Séance de révision pour les examens.");
        dto.setCity("Dakar");
        dto.setLocation("Bibliothèque");
        dto.setMeetingType(MeetingType.ONLINE);
        dto.setMeetingDate(LocalDateTime.of(2026, 8, 1, 10, 0));
        dto.setMaxMembers(8);
        dto.setAvailable(true);
        dto.setCreatorId(1L);
        dto.setCreatorFirstName("Alice");
        dto.setCreatorLastName("Dupont");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        return dto;
    }
}
