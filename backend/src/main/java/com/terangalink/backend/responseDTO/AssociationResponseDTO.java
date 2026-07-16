package com.terangalink.backend.responseDTO;

import com.terangalink.backend.enums.AssociationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
ASSOCIATION RESPONSE DTO

Represente les informations
retournees au client.
*/

@Getter
@Setter
@NoArgsConstructor

public class AssociationResponseDTO {

    private Long id;

    private String title;

    private String description;

    private String city;

    private String address;

    private String contactEmail;

    private String phone;

    private String website;

    private String logoUrl;

    private AssociationType associationType;

    private boolean available;

    private Long creatorId;

    private String creatorFirstName;

    private String creatorLastName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
