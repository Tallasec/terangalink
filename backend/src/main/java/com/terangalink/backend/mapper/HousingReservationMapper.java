package com.terangalink.backend.mapper;

import com.terangalink.backend.entity.HousingReservation;
import com.terangalink.backend.responseDTO.HousingReservationResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class HousingReservationMapper {

    public HousingReservationResponseDTO toResponseDto(HousingReservation reservation) {
        HousingReservationResponseDTO dto = new HousingReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setPhoneNumber(reservation.getPhoneNumber());
        dto.setMessage(reservation.getMessage());
        dto.setStatus(reservation.getStatus());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());

        if (reservation.getHousing() != null) {
            dto.setHousingId(reservation.getHousing().getId());
            dto.setHousingTitle(reservation.getHousing().getTitle());
        }

        if (reservation.getTenant() != null) {
            dto.setTenantId(reservation.getTenant().getId());
            dto.setTenantFirstName(reservation.getTenant().getFirstName());
            dto.setTenantLastName(reservation.getTenant().getLastName());
        }

        return dto;
    }
}
