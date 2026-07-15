package com.terangalink.backend.responseDTO;

import com.terangalink.backend.enums.ContractType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
JOB POST RESPONSE DTO

Représente les informations
retournées au client.
*/

@Getter
@Setter
@NoArgsConstructor

public class JobPostResponseDTO {

    private Long id;

    private String title;

    private String description;

    private String companyName;

    private String city;

    private String address;

    private ContractType contractType;

    private BigDecimal salary;

    private boolean available;

    private Long ownerId;

    private String ownerFirstName;

    private String ownerLastName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
