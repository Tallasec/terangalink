package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.AssociationType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
UPDATE ASSOCIATION REQUEST DTO

Represente les donnees utilisees
pour modifier une association.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class UpdateAssociationRequestDTO {

    // Titre de l'association
    @Size(max = 150, message = "Le titre ne doit pas depasser 150 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le titre ne peut pas etre vide.")
    private String title;

    // Description
    @Size(max = 10000, message = "La description ne doit pas depasser 10000 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La description ne peut pas etre vide.")
    private String description;

    // Ville
    @Size(max = 120, message = "La ville ne doit pas depasser 120 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La ville ne peut pas etre vide.")
    private String city;

    // Adresse
    @Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "L'adresse ne peut pas etre vide.")
    private String address;

    // Email de contact
    @Size(max = 255, message = "L'email de contact ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "L'email de contact ne peut pas etre vide.")
    private String contactEmail;

    // Telephone
    @Size(max = 30, message = "Le telephone ne doit pas depasser 30 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le telephone ne peut pas etre vide.")
    private String phone;

    // Site web
    @Size(max = 255, message = "Le site web ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le site web ne peut pas etre vide.")
    private String website;

    // Logo
    @Size(max = 255, message = "L'URL du logo ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "L'URL du logo ne peut pas etre vide.")
    private String logoUrl;

    // Type d'association
    private AssociationType associationType;

    // Association disponible
    private Boolean available;
}
