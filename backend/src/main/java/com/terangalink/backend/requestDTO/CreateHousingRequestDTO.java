package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.HousingType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateHousingRequestDTO {

    @NotBlank(message = "Le titre du logement est obligatoire.")
    @Size(max = 150, message = "Le titre du logement ne doit pas depasser 150 caracteres.")
    private String title;

    @Size(max = 5000, message = "La description du logement ne doit pas depasser 5000 caracteres.")
    private String description;

    @NotBlank(message = "La ville du logement est obligatoire.")
    @Size(max = 100, message = "La ville du logement ne doit pas depasser 100 caracteres.")
    private String city;

    @Size(max = 255, message = "L'adresse du logement ne doit pas depasser 255 caracteres.")
    private String address;

    @NotNull(message = "Le prix du logement est obligatoire.")
    @DecimalMin(value = "0.00", inclusive = false, message = "Le prix du logement doit etre superieur a 0.")
    @Digits(integer = 8, fraction = 2, message = "Le prix du logement doit contenir au maximum 8 chiffres entiers et 2 decimales.")
    private BigDecimal price;

    @NotNull(message = "Le type de logement est obligatoire.")
    private HousingType housingType;

    private Boolean available;
}
