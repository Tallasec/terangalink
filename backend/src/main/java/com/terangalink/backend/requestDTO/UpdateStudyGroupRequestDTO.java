package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.MeetingType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
UPDATE STUDY GROUP REQUEST DTO

Représente les données utilisées
pour modifier un groupe.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class UpdateStudyGroupRequestDTO {

    // Titre du groupe
    @Size(max = 150, message = "Le titre ne doit pas depasser 150 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le titre ne peut pas etre vide.")
    private String title;

    // Matière
    @Size(max = 150, message = "La matière ne doit pas depasser 150 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La matière ne peut pas etre vide.")
    private String subject;

    // Description
    @Size(max = 10000, message = "La description ne doit pas depasser 10000 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La description ne peut pas etre vide.")
    private String description;

    // Ville
    @Size(max = 120, message = "La ville ne doit pas depasser 120 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La ville ne peut pas etre vide.")
    private String city;

    // Lieu de rendez-vous
    @Size(max = 255, message = "Le lieu ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le lieu ne peut pas etre vide.")
    private String location;

    // Type de rencontre
    private MeetingType meetingType;

    // Date de la séance
    @Future(message = "La date de la séance doit etre dans le futur.")
    private LocalDateTime meetingDate;

    // Nombre maximum de participants
    @Min(value = 2, message = "Le nombre maximum de participants doit etre au moins 2.")
    private Integer maxMembers;

    // Groupe disponible
    private Boolean available;
}
