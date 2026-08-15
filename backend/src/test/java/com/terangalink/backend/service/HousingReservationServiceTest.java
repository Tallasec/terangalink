package com.terangalink.backend.service;

import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.entity.HousingReservation;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.HousingReservationStatus;
import com.terangalink.backend.exception.business.HousingNotAvailableException;
import com.terangalink.backend.exception.business.InvalidHousingReservationException;
import com.terangalink.backend.mapper.HousingReservationMapper;
import com.terangalink.backend.repository.HousingRepository;
import com.terangalink.backend.repository.HousingReservationRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateHousingReservationRequestDTO;
import com.terangalink.backend.responseDTO.HousingReservationResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HousingReservationServiceTest {

    @Mock
    private HousingReservationRepository housingReservationRepository;

    @Mock
    private HousingRepository housingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HousingReservationMapper housingReservationMapper;

    @InjectMocks
    private HousingReservationService housingReservationService;

    @Captor
    private ArgumentCaptor<HousingReservation> reservationCaptor;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        mocks.close();
    }

    @Test
    void reserveHousing_successful() {
        Long housingId = 10L;
        User owner = new User();
        owner.setId(1L);
        owner.setRole(com.terangalink.backend.enums.Role.USER);
        User tenant = new User();
        tenant.setId(2L);
        tenant.setRole(com.terangalink.backend.enums.Role.USER);

        HousingPost housing = new HousingPost();
        housing.setId(housingId);
        housing.setOwner(owner);
        housing.setAvailable(true);

        // set authentication principal
        UserPrincipal principal = UserPrincipal.from(tenant);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        when(userRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(housingRepository.findById(housingId)).thenReturn(Optional.of(housing));
        when(housingReservationRepository.findByHousingIdAndTenantIdAndStatus(housingId, tenant.getId(),
                HousingReservationStatus.CONFIRMED))
                .thenReturn(Optional.empty());

        HousingReservation saved = new HousingReservation();
        saved.setId(5L);
        saved.setStatus(HousingReservationStatus.CONFIRMED);
        when(housingReservationRepository.save(any(HousingReservation.class))).thenReturn(saved);

        HousingReservationResponseDTO dto = new HousingReservationResponseDTO();
        dto.setId(5L);
        when(housingReservationMapper.toResponseDto(saved)).thenReturn(dto);

        CreateHousingReservationRequestDTO request = new CreateHousingReservationRequestDTO();
        request.setPhoneNumber("0600000000");
        request.setMessage("Hello");

        HousingReservationResponseDTO response = housingReservationService.reserveHousing(housingId, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(5L);
        verify(housingReservationRepository).save(reservationCaptor.capture());
        HousingReservation captured = reservationCaptor.getValue();
        assertThat(captured.getTenant().getId()).isEqualTo(tenant.getId());
    }

    @Test
    void reserveHousing_shouldThrow_whenOwnerTriesToReserve() {
        Long housingId = 10L;
        User owner = new User();
        owner.setId(2L);

        HousingPost housing = new HousingPost();
        housing.setId(housingId);
        housing.setOwner(owner);

        owner.setRole(com.terangalink.backend.enums.Role.USER);
        UserPrincipal principal = UserPrincipal.from(owner);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(housingRepository.findById(housingId)).thenReturn(Optional.of(housing));

        CreateHousingReservationRequestDTO request = new CreateHousingReservationRequestDTO();
        request.setPhoneNumber("0600000000");
        request.setMessage("Hello");

        assertThrows(InvalidHousingReservationException.class,
                () -> housingReservationService.reserveHousing(housingId, request));
    }

    @Test
    void reserveHousing_shouldThrow_whenHousingNotAvailable() {
        Long housingId = 10L;
        User tenant = new User();
        tenant.setId(2L);
        tenant.setRole(com.terangalink.backend.enums.Role.USER);

        HousingPost housing = new HousingPost();
        housing.setId(housingId);
        housing.setAvailable(false);

        UserPrincipal principal = UserPrincipal.from(tenant);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        when(userRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(housingRepository.findById(housingId)).thenReturn(Optional.of(housing));

        CreateHousingReservationRequestDTO request = new CreateHousingReservationRequestDTO();
        request.setPhoneNumber("0600000000");
        request.setMessage("Hello");

        assertThrows(HousingNotAvailableException.class,
                () -> housingReservationService.reserveHousing(housingId, request));
    }
}
