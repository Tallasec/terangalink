package com.terangalink.backend.service;

import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.entity.HousingReservation;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.HousingReservationStatus;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.exception.business.HousingNotAvailableException;
import com.terangalink.backend.exception.business.HousingNotFoundException;
import com.terangalink.backend.exception.business.HousingReservationNotFoundException;
import com.terangalink.backend.exception.business.InvalidHousingReservationException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.HousingReservationMapper;
import com.terangalink.backend.repository.HousingRepository;
import com.terangalink.backend.repository.HousingReservationRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateHousingReservationRequestDTO;
import com.terangalink.backend.responseDTO.HousingReservationResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HousingReservationService {

    private final HousingReservationRepository housingReservationRepository;
    private final HousingRepository housingRepository;
    private final UserRepository userRepository;
    private final HousingReservationMapper housingReservationMapper;

    public HousingReservationService(
            HousingReservationRepository housingReservationRepository,
            HousingRepository housingRepository,
            UserRepository userRepository,
            HousingReservationMapper housingReservationMapper) {
        this.housingReservationRepository = housingReservationRepository;
        this.housingRepository = housingRepository;
        this.userRepository = userRepository;
        this.housingReservationMapper = housingReservationMapper;
    }

    @Transactional
    public HousingReservationResponseDTO reserveHousing(
            Long housingId,
            CreateHousingReservationRequestDTO request) {
        if (request == null) {
            throw new InvalidHousingReservationException(
                    "Le numero de telephone et le message sont obligatoires.");
        }

        UserPrincipal principal = getCurrentPrincipal();
        User tenant = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));
        HousingPost housing = findHousingByIdOrThrow(housingId);

        if (housing.getOwner() != null && housing.getOwner().getId().equals(tenant.getId())) {
            throw new InvalidHousingReservationException("Vous ne pouvez pas reserver votre propre logement.");
        }

        if (!housing.isAvailable()) {
            throw new HousingNotAvailableException("Ce logement n'est plus disponible.");
        }

        if (housingReservationRepository
                .findByHousingIdAndTenantIdAndStatus(
                        housingId, tenant.getId(), HousingReservationStatus.CONFIRMED)
                .isPresent()) {
            throw new InvalidHousingReservationException("Vous avez deja reserve ce logement.");
        }

        HousingReservation reservation = new HousingReservation();
        reservation.setHousing(housing);
        reservation.setTenant(tenant);
        reservation.setPhoneNumber(request.getPhoneNumber().trim());
        reservation.setMessage(request.getMessage().trim());
        reservation.setStatus(HousingReservationStatus.CONFIRMED);

        return housingReservationMapper.toResponseDto(housingReservationRepository.save(reservation));
    }

    public HousingReservationResponseDTO getMyReservationForHousing(Long housingId) {
        UserPrincipal principal = getCurrentPrincipal();

        return housingReservationRepository
                .findByHousingIdAndTenantIdAndStatus(
                        housingId, principal.getId(), HousingReservationStatus.CONFIRMED)
                .map(housingReservationMapper::toResponseDto)
                .orElse(null);
    }

    public List<HousingReservationResponseDTO> getMyReservations() {
        UserPrincipal principal = getCurrentPrincipal();

        return housingReservationRepository.findByTenantIdOrderByCreatedAtDesc(principal.getId()).stream()
                .map(housingReservationMapper::toResponseDto)
                .toList();
    }

    public List<HousingReservationResponseDTO> getHousingReservations(Long housingId) {
        findHousingByIdOrThrow(housingId);

        return housingReservationRepository.findByHousingIdOrderByCreatedAtDesc(housingId).stream()
                .map(housingReservationMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public HousingReservationResponseDTO cancelReservation(Long reservationId) {
        HousingReservation reservation = findReservationByIdOrThrow(reservationId);
        UserPrincipal principal = getCurrentPrincipal();

        if (!canManageReservation(reservation, principal)) {
            throw new InvalidHousingReservationException(
                    "Vous n'etes pas autorise a annuler cette reservation.");
        }

        if (reservation.getStatus() == HousingReservationStatus.CANCELLED) {
            throw new InvalidHousingReservationException("Cette reservation est deja annulee.");
        }

        reservation.setStatus(HousingReservationStatus.CANCELLED);
        HousingReservation savedReservation = housingReservationRepository.save(reservation);

        return housingReservationMapper.toResponseDto(savedReservation);
    }

    private boolean canManageReservation(HousingReservation reservation, UserPrincipal principal) {
        if (principal.getRole() == Role.ADMIN) {
            return true;
        }

        if (reservation.getTenant() != null
                && reservation.getTenant().getId().equals(principal.getId())) {
            return true;
        }

        return reservation.getHousing() != null
                && reservation.getHousing().getOwner() != null
                && reservation.getHousing().getOwner().getId().equals(principal.getId());
    }

    private UserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new IllegalStateException("Utilisateur non authentifie.");
        }

        return principal;
    }

    private HousingPost findHousingByIdOrThrow(Long housingId) {
        return housingRepository.findById(housingId)
                .orElseThrow(() -> new HousingNotFoundException(
                        "Logement introuvable avec l'id : " + housingId));
    }

    private HousingReservation findReservationByIdOrThrow(Long reservationId) {
        return housingReservationRepository.findById(reservationId)
                .orElseThrow(() -> new HousingReservationNotFoundException(
                        "Reservation introuvable avec l'id : " + reservationId));
    }
}
