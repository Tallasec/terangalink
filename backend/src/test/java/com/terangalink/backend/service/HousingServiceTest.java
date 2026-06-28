package com.terangalink.backend.service;

import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.HousingType;
import com.terangalink.backend.exception.business.HousingNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.HousingMapper;
import com.terangalink.backend.repository.HousingRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateHousingRequestDTO;
import com.terangalink.backend.requestDTO.UpdateHousingRequestDTO;
import com.terangalink.backend.responseDTO.HousingResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HousingServiceTest {

    @Mock
    private HousingRepository housingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HousingMapper housingMapper;

    @InjectMocks
    private HousingService housingService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createHousing_shouldPersistHousingForCurrentUser() {
        CreateHousingRequestDTO request = validCreateRequest();
        User owner = UserTestFixtures.sampleUser(1L);
        HousingPost housingPost = sampleHousing(null, owner);
        HousingPost savedHousing = sampleHousing(10L, owner);
        HousingResponseDTO response = sampleHousingResponse(10L, owner.getId());

        authenticate(owner);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(housingMapper.toEntity(request)).thenReturn(housingPost);
        when(housingRepository.save(housingPost)).thenReturn(savedHousing);
        when(housingMapper.toResponseDto(savedHousing)).thenReturn(response);

        HousingResponseDTO result = housingService.createHousing(request);

        assertThat(result).isEqualTo(response);
        assertThat(housingPost.getOwner()).isEqualTo(owner);
        verify(housingRepository).save(housingPost);
    }

    @Test
    void createHousing_shouldThrowWhenCurrentUserDoesNotExist() {
        CreateHousingRequestDTO request = validCreateRequest();
        User principalUser = UserTestFixtures.sampleUser(1L);

        authenticate(principalUser);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> housingService.createHousing(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Utilisateur introuvable avec l'id : 1");

        verify(housingRepository, never()).save(any(HousingPost.class));
    }

    @Test
    void getHousingById_shouldReturnHousing() {
        User owner = UserTestFixtures.sampleUser(1L);
        HousingPost housingPost = sampleHousing(10L, owner);
        HousingResponseDTO response = sampleHousingResponse(10L, owner.getId());

        when(housingRepository.findById(10L)).thenReturn(Optional.of(housingPost));
        when(housingMapper.toResponseDto(housingPost)).thenReturn(response);

        assertThat(housingService.getHousingById(10L)).isEqualTo(response);
    }

    @Test
    void getHousingById_shouldThrowWhenHousingNotFound() {
        when(housingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> housingService.getHousingById(99L))
                .isInstanceOf(HousingNotFoundException.class)
                .hasMessage("Logement introuvable avec l'id : 99");
    }

    @Test
    void getAllHousings_shouldReturnMappedPage() {
        PageRequest pageable = PageRequest.of(0, 20);
        User owner = UserTestFixtures.sampleUser(1L);
        HousingPost housingPost = sampleHousing(10L, owner);
        HousingResponseDTO response = sampleHousingResponse(10L, owner.getId());
        Page<HousingPost> housingPage = new PageImpl<>(List.of(housingPost), pageable, 1);

        when(housingRepository.findAll(pageable)).thenReturn(housingPage);
        when(housingMapper.toResponseDto(housingPost)).thenReturn(response);

        Page<HousingResponseDTO> result = housingService.getAllHousings(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateHousing_shouldPatchExistingHousingAndUpdateTimestamp() {
        UpdateHousingRequestDTO request = validUpdateRequest();
        User owner = UserTestFixtures.sampleUser(1L);
        HousingPost housingPost = sampleHousing(10L, owner);
        HousingPost savedHousing = sampleHousing(10L, owner);
        HousingResponseDTO response = sampleHousingResponse(10L, owner.getId());

        when(housingRepository.findById(10L)).thenReturn(Optional.of(housingPost));
        when(housingRepository.save(housingPost)).thenReturn(savedHousing);
        when(housingMapper.toResponseDto(savedHousing)).thenReturn(response);

        HousingResponseDTO result = housingService.updateHousing(10L, request);

        assertThat(result).isEqualTo(response);
        assertThat(housingPost.getUpdatedAt()).isNotNull();
        verify(housingMapper).updateEntity(housingPost, request);
        verify(housingRepository).save(housingPost);
    }

    @Test
    void updateHousing_shouldThrowWhenHousingNotFound() {
        UpdateHousingRequestDTO request = validUpdateRequest();
        when(housingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> housingService.updateHousing(99L, request))
                .isInstanceOf(HousingNotFoundException.class)
                .hasMessage("Logement introuvable avec l'id : 99");

        verify(housingMapper, never()).updateEntity(any(HousingPost.class), any(UpdateHousingRequestDTO.class));
        verify(housingRepository, never()).save(any(HousingPost.class));
    }

    @Test
    void deleteHousing_shouldDeleteExistingHousing() {
        User owner = UserTestFixtures.sampleUser(1L);
        HousingPost housingPost = sampleHousing(10L, owner);

        when(housingRepository.findById(10L)).thenReturn(Optional.of(housingPost));

        housingService.deleteHousing(10L);

        verify(housingRepository).delete(housingPost);
    }

    @Test
    void deleteHousing_shouldThrowWhenHousingNotFound() {
        when(housingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> housingService.deleteHousing(99L))
                .isInstanceOf(HousingNotFoundException.class)
                .hasMessage("Logement introuvable avec l'id : 99");

        verify(housingRepository, never()).delete(any(HousingPost.class));
    }

    private void authenticate(User user) {
        UserPrincipal principal = UserPrincipal.from(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private CreateHousingRequestDTO validCreateRequest() {
        CreateHousingRequestDTO request = new CreateHousingRequestDTO();
        request.setTitle("Studio meuble proche campus");
        request.setDescription("Studio lumineux et calme.");
        request.setCity("Paris");
        request.setAddress("12 rue des Etudiants");
        request.setPrice(new BigDecimal("750.00"));
        request.setHousingType(HousingType.STUDIO);
        request.setAvailable(true);
        return request;
    }

    private UpdateHousingRequestDTO validUpdateRequest() {
        UpdateHousingRequestDTO request = new UpdateHousingRequestDTO();
        request.setTitle("Studio meuble renove");
        request.setPrice(new BigDecimal("780.00"));
        return request;
    }

    private HousingPost sampleHousing(Long id, User owner) {
        HousingPost housingPost = new HousingPost();
        housingPost.setId(id);
        housingPost.setTitle("Studio meuble proche campus");
        housingPost.setDescription("Studio lumineux et calme.");
        housingPost.setCity("Paris");
        housingPost.setAddress("12 rue des Etudiants");
        housingPost.setPrice(new BigDecimal("750.00"));
        housingPost.setHousingType(HousingType.STUDIO);
        housingPost.setAvailable(true);
        housingPost.setOwner(owner);
        housingPost.setCreatedAt(LocalDateTime.of(2025, 2, 1, 9, 0));
        return housingPost;
    }

    private HousingResponseDTO sampleHousingResponse(Long id, Long ownerId) {
        HousingResponseDTO response = new HousingResponseDTO();
        response.setId(id);
        response.setTitle("Studio meuble proche campus");
        response.setDescription("Studio lumineux et calme.");
        response.setCity("Paris");
        response.setAddress("12 rue des Etudiants");
        response.setPrice(new BigDecimal("750.00"));
        response.setHousingType(HousingType.STUDIO);
        response.setAvailable(true);
        response.setOwnerId(ownerId);
        response.setCreatedAt(LocalDateTime.of(2025, 2, 1, 9, 0));
        return response;
    }

    @Test
    void searchHousings_shouldReturnFilteredPage() {

        PageRequest pageable = PageRequest.of(0, 20);

        User owner = UserTestFixtures.sampleUser(1L);

        HousingPost housingPost = sampleHousing(10L, owner);

        HousingResponseDTO response = sampleHousingResponse(10L, owner.getId());

        Page<HousingPost> housingPage =
                new PageImpl<>(List.of(housingPost), pageable, 1);

        when(housingRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(housingPage);

        when(housingMapper.toResponseDto(housingPost))
                .thenReturn(response);

        Page<HousingResponseDTO> result = housingService.searchHousings(
                "Paris",
                HousingType.STUDIO,
                true,
                new BigDecimal("500"),
                new BigDecimal("800"),
                pageable
        );

        assertThat(result.getContent()).containsExactly(response);

        verify(housingRepository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchHousings_shouldReturnEmptyPage() {

        PageRequest pageable = PageRequest.of(0, 20);

        when(housingRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        Page<HousingResponseDTO> result = housingService.searchHousings(
                "Lyon",
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(result.getContent()).isEmpty();

        verify(housingRepository)
                .findAll(any(Specification.class), eq(pageable));
    }
}
