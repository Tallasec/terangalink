package com.terangalink.backend.service;

import com.terangalink.backend.entity.Association;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.AssociationType;
import com.terangalink.backend.exception.business.AssociationNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.AssociationMapper;
import com.terangalink.backend.repository.AssociationRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateAssociationRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAssociationRequestDTO;
import com.terangalink.backend.responseDTO.AssociationResponseDTO;
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
class AssociationServiceTest {

    @Mock
    private AssociationRepository associationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssociationMapper associationMapper;

    @InjectMocks
    private AssociationService associationService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createAssociation_shouldCreateAssociation() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        setAuthentication(principal);

        User creator = UserTestFixtures.sampleUser(1L);
        CreateAssociationRequestDTO request = createRequest();
        Association association = sampleAssociation(null, creator);
        Association savedAssociation = sampleAssociation(10L, creator);
        AssociationResponseDTO response = sampleResponse(10L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(creator));
        when(associationMapper.toEntity(request))
                .thenReturn(association);
        when(associationRepository.save(association))
                .thenReturn(savedAssociation);
        when(associationMapper.toResponseDto(savedAssociation))
                .thenReturn(response);

        AssociationResponseDTO result =
                associationService.createAssociation(request);

        assertThat(result).isEqualTo(response);
        assertThat(association.getCreator()).isEqualTo(creator);
        verify(associationRepository).save(association);
    }

    @Test
    void createAssociation_shouldThrowWhenCurrentUserDoesNotExist() {

        CreateAssociationRequestDTO request = createRequest();
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        setAuthentication(principal);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                associationService.createAssociation(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Utilisateur introuvable avec l'id : 1");

        verify(associationRepository, never()).save(any(Association.class));
    }

    @Test
    void getAssociationById_shouldReturnAssociation() {

        User creator = UserTestFixtures.sampleUser(1L);
        Association association = sampleAssociation(10L, creator);
        AssociationResponseDTO response = sampleResponse(10L);

        when(associationRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(association));
        when(associationMapper.toResponseDto(association))
                .thenReturn(response);

        assertThat(associationService.getAssociationById(10L)).isEqualTo(response);
    }

    @Test
    void getAssociationById_shouldThrowWhenAssociationNotFound() {

        when(associationRepository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                associationService.getAssociationById(99L))
                .isInstanceOf(AssociationNotFoundException.class)
                .hasMessage("Association introuvable avec l'id : 99");
    }

    @Test
    void getAllAssociations_shouldReturnMappedPage() {

        PageRequest pageable = PageRequest.of(0, 20);
        User creator = UserTestFixtures.sampleUser(1L);
        Association association = sampleAssociation(10L, creator);
        AssociationResponseDTO response = sampleResponse(10L);
        Page<Association> page = new PageImpl<>(List.of(association), pageable, 1);

        when(associationRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(associationMapper.toResponseDto(association))
                .thenReturn(response);

        Page<AssociationResponseDTO> result = associationService.getAllAssociations(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchAssociations_shouldReturnMappedPage() {

        PageRequest pageable = PageRequest.of(0, 20);
        User creator = UserTestFixtures.sampleUser(1L);
        Association association = sampleAssociation(10L, creator);
        AssociationResponseDTO response = sampleResponse(10L);
        Page<Association> page = new PageImpl<>(List.of(association), pageable, 1);

        when(associationRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(associationMapper.toResponseDto(association))
                .thenReturn(response);

        Page<AssociationResponseDTO> result = associationService.searchAssociations(
                "TerangaLink Association",
                "Dakar",
                AssociationType.DAHIRA,
                true,
                pageable
        );

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void updateAssociation_shouldPatchAssociation() {

        User creator = UserTestFixtures.sampleUser(1L);
        Association association = sampleAssociation(10L, creator);
        UpdateAssociationRequestDTO request = updateRequest();
        AssociationResponseDTO response = sampleResponse(10L);

        when(associationRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(association));
        when(associationRepository.save(association))
                .thenReturn(association);
        when(associationMapper.toResponseDto(association))
                .thenReturn(response);

        AssociationResponseDTO result = associationService.updateAssociation(10L, request);

        assertThat(result).isEqualTo(response);
        verify(associationMapper).updateEntity(association, request);
    }

    @Test
    void deleteAssociation_shouldSoftDeleteAssociation() {

        User creator = UserTestFixtures.sampleUser(1L);
        Association association = sampleAssociation(10L, creator);

        when(associationRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(association));
        when(associationRepository.save(association))
                .thenReturn(association);

        associationService.deleteAssociation(10L);

        assertThat(association.isDeleted()).isTrue();
        verify(associationRepository).save(association);
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private CreateAssociationRequestDTO createRequest() {

        CreateAssociationRequestDTO dto =
                new CreateAssociationRequestDTO();

        dto.setTitle("TerangaLink Association");
        dto.setDescription("Association etudiante et culturelle.");
        dto.setCity("Dakar");
        dto.setAddress("Plateau");
        dto.setContactEmail("contact@terangalink.org");
        dto.setPhone("+221770000000");
        dto.setWebsite("https://terangalink.org");
        dto.setLogoUrl("https://terangalink.org/logo.png");
        dto.setAssociationType(AssociationType.DAHIRA);
        dto.setAvailable(true);

        return dto;
    }

    private UpdateAssociationRequestDTO updateRequest() {

        UpdateAssociationRequestDTO dto =
                new UpdateAssociationRequestDTO();

        dto.setTitle("TerangaLink Association Renovee");
        dto.setAvailable(false);

        return dto;
    }

    private Association sampleAssociation(
            Long id,
            User creator
    ) {

        Association association = new Association();

        association.setId(id);
        association.setTitle("TerangaLink Association");
        association.setDescription("Association etudiante et culturelle.");
        association.setCity("Dakar");
        association.setAddress("Plateau");
        association.setContactEmail("contact@terangalink.org");
        association.setPhone("+221770000000");
        association.setWebsite("https://terangalink.org");
        association.setLogoUrl("https://terangalink.org/logo.png");
        association.setAssociationType(AssociationType.DAHIRA);
        association.setAvailable(true);
        association.setDeleted(false);
        association.setCreator(creator);
        association.setCreatedAt(LocalDateTime.now());
        association.setUpdatedAt(LocalDateTime.now());

        return association;
    }

    private AssociationResponseDTO sampleResponse(Long id) {

        AssociationResponseDTO dto =
                new AssociationResponseDTO();

        dto.setId(id);
        dto.setTitle("TerangaLink Association");
        dto.setDescription("Association etudiante et culturelle.");
        dto.setCity("Dakar");
        dto.setAddress("Plateau");
        dto.setContactEmail("contact@terangalink.org");
        dto.setPhone("+221770000000");
        dto.setWebsite("https://terangalink.org");
        dto.setLogoUrl("https://terangalink.org/logo.png");
        dto.setAssociationType(AssociationType.DAHIRA);
        dto.setAvailable(true);
        dto.setCreatorId(1L);
        dto.setCreatorFirstName("Alice");
        dto.setCreatorLastName("Dupont");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        return dto;
    }
}
