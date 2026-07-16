package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.AssociationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
CREATE ASSOCIATION REQUEST DTO

Represente les donnees necessaires
a la creation d'une association.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class CreateAssociationRequestDTO {

    // Titre de l'association
    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 150, message = "Le titre ne doit pas depasser 150 caracteres.")
    private String title;

    // Description
    @NotBlank(message = "La description est obligatoire.")
    @Size(max = 10000, message = "La description ne doit pas depasser 10000 caracteres.")
    private String description;

    // Ville
    @NotBlank(message = "La ville est obligatoire.")
    @Size(max = 120, message = "La ville ne doit pas depasser 120 caracteres.")
    private String city;

    // Adresse
    @Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres.")
    private String address;

    // Email de contact
    @Size(max = 255, message = "L'email de contact ne doit pas depasser 255 caracteres.")
    private String contactEmail;

    // Telephone
    @Size(max = 30, message = "Le telephone ne doit pas depasser 30 caracteres.")
    private String phone;

    // Site web
    @Size(max = 255, message = "Le site web ne doit pas depasser 255 caracteres.")
    private String website;

    // Logo
    @Size(max = 255, message = "L'URL du logo ne doit pas depasser 255 caracteres.")
    private String logoUrl;

    // Type d'association
    @NotNull(message = "Le type d'association est obligatoire.")
    private AssociationType associationType;

    // Association disponible
    private Boolean available;
}
