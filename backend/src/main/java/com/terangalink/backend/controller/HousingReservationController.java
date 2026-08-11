package com.terangalink.backend.controller;

import com.terangalink.backend.requestDTO.CreateHousingReservationRequestDTO;
import com.terangalink.backend.responseDTO.HousingReservationResponseDTO;
import com.terangalink.backend.service.HousingReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HousingReservationController {

    private final HousingReservationService housingReservationService;

    public HousingReservationController(HousingReservationService housingReservationService) {
        this.housingReservationService = housingReservationService;
    }

    @PostMapping("/housings/{housingId}/reservations")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<HousingReservationResponseDTO> reserveHousing(
            @PathVariable Long housingId,
            @RequestBody @Valid CreateHousingReservationRequestDTO request) {
        HousingReservationResponseDTO response = housingReservationService.reserveHousing(housingId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/../../housing-reservations/{id}")
                        .buildAndExpand(response.getId())
                        .toUri())
                .body(response);
    }

    @GetMapping("/housings/{housingId}/reservations/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<HousingReservationResponseDTO> getMyReservationForHousing(
            @PathVariable Long housingId) {
        HousingReservationResponseDTO response =
                housingReservationService.getMyReservationForHousing(housingId);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/housings/{housingId}/reservations")
    @PreAuthorize("@housingReservationSecurityService.canViewHousingReservations(#housingId)")
    public ResponseEntity<List<HousingReservationResponseDTO>> getHousingReservations(
            @PathVariable Long housingId) {
        return ResponseEntity.ok(housingReservationService.getHousingReservations(housingId));
    }

    @GetMapping("/housing-reservations/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<HousingReservationResponseDTO>> getMyReservations() {
        return ResponseEntity.ok(housingReservationService.getMyReservations());
    }

    @PostMapping("/housing-reservations/{reservationId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<HousingReservationResponseDTO> cancelReservation(
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(housingReservationService.cancelReservation(reservationId));
    }
}
