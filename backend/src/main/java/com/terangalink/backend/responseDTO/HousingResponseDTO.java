package com.terangalink.backend.responseDTO;

import com.terangalink.backend.enums.HousingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HousingResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String city;
    private String address;
    private BigDecimal price;
    private HousingType housingType;
    private boolean available;
    private Long ownerId;
    private String ownerFirstName;
    private String ownerLastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<HousingImageResponseDTO> images = new ArrayList<>();
}
