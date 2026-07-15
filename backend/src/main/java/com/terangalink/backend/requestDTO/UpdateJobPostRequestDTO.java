package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.ContractType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/*
UPDATE JOB POST REQUEST DTO

Représente les données utilisées
pour modifier une offre.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class UpdateJobPostRequestDTO {

    // Titre de l'offre
    @Size(max = 150, message = "Le titre ne doit pas depasser 150 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le titre ne peut pas etre vide.")
    private String title;

    // Description de l'offre
    @Size(max = 10000, message = "La description ne doit pas depasser 10000 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La description ne peut pas etre vide.")
    private String description;

    // Nom de l'entreprise
    @Size(max = 150, message = "Le nom de l'entreprise ne doit pas depasser 150 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le nom de l'entreprise ne peut pas etre vide.")
    private String companyName;

    // Ville
    @Size(max = 120, message = "La ville ne doit pas depasser 120 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La ville ne peut pas etre vide.")
    private String city;

    // Adresse
    @Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "L'adresse ne peut pas etre vide.")
    private String address;

    // Type de contrat
    private ContractType contractType;

    // Salaire
    @DecimalMin(value = "0.00", inclusive = false, message = "Le salaire doit etre superieur a 0.")
    @Digits(integer = 10, fraction = 2, message = "Le salaire doit contenir au maximum 10 chiffres entiers et 2 decimales.")
    private BigDecimal salary;

    // Offre disponible
    private Boolean available;
}
