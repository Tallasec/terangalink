package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.ContractType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/*
CREATE JOB POST REQUEST DTO

Représente les données nécessaires
à la création d'une offre.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class CreateJobPostRequestDTO {

    // Titre de l'offre
    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 150, message = "Le titre ne doit pas depasser 150 caracteres.")
    private String title;

    // Description de l'offre
    @NotBlank(message = "La description est obligatoire.")
    @Size(max = 10000, message = "La description ne doit pas depasser 10000 caracteres.")
    private String description;

    // Nom de l'entreprise
    @NotBlank(message = "Le nom de l'entreprise est obligatoire.")
    @Size(max = 150, message = "Le nom de l'entreprise ne doit pas depasser 150 caracteres.")
    private String companyName;

    // Ville
    @NotBlank(message = "La ville est obligatoire.")
    @Size(max = 120, message = "La ville ne doit pas depasser 120 caracteres.")
    private String city;

    // Adresse
    @Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres.")
    private String address;

    // Type de contrat
    @NotNull(message = "Le type de contrat est obligatoire.")
    private ContractType contractType;

    // Salaire
    @NotNull(message = "Le salaire est obligatoire.")
    @DecimalMin(value = "0.00", inclusive = false, message = "Le salaire doit etre superieur a 0.")
    @Digits(integer = 10, fraction = 2, message = "Le salaire doit contenir au maximum 10 chiffres entiers et 2 decimales.")
    private BigDecimal salary;

    // Offre disponible
    private Boolean available;
}
