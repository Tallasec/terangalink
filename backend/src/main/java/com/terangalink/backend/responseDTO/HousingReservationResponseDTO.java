package com.terangalink.backend.responseDTO;

import com.terangalink.backend.enums.HousingReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class HousingReservationResponseDTO {

    private Long id;
    private Long housingId;
    private String housingTitle;
    private Long tenantId;
    private String tenantFirstName;
    private String tenantLastName;
    private String phoneNumber;
    private String message;
    private HousingReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
