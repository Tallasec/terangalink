package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.MeetingType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
CREATE STUDY GROUP REQUEST DTO

Représente les données nécessaires
à la création d'un groupe.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class CreateStudyGroupRequestDTO {

    // Titre du groupe
    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 150, message = "Le titre ne doit pas depasser 150 caracteres.")
    private String title;

    // Matière
    @NotBlank(message = "La matière est obligatoire.")
    @Size(max = 150, message = "La matière ne doit pas depasser 150 caracteres.")
    private String subject;

    // Description
    @NotBlank(message = "La description est obligatoire.")
    @Size(max = 10000, message = "La description ne doit pas depasser 10000 caracteres.")
    private String description;

    // Ville
    @NotBlank(message = "La ville est obligatoire.")
    @Size(max = 120, message = "La ville ne doit pas depasser 120 caracteres.")
    private String city;

    // Lieu de rendez-vous
    @Size(max = 255, message = "Le lieu ne doit pas depasser 255 caracteres.")
    private String location;

    // Type de rencontre
    @NotNull(message = "Le type de rencontre est obligatoire.")
    private MeetingType meetingType;

    // Date de la séance
    @NotNull(message = "La date de la séance est obligatoire.")
    @Future(message = "La date de la séance doit etre dans le futur.")
    private LocalDateTime meetingDate;

    // Nombre maximum de participants
    @NotNull(message = "Le nombre maximum de participants est obligatoire.")
    @Min(value = 2, message = "Le nombre maximum de participants doit etre au moins 2.")
    private Integer maxMembers;

    // Groupe disponible
    private Boolean available;
}
