package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.HousingType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateHousingRequestDTO {

    @Size(max = 150, message = "Le titre du logement ne doit pas depasser 150 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le titre du logement ne peut pas etre vide ou blanc.")
    private String title;

    @Size(max = 5000, message = "La description du logement ne doit pas depasser 5000 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La description du logement ne peut pas etre vide ou blanche.")
    private String description;

    @Size(max = 100, message = "La ville du logement ne doit pas depasser 100 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La ville du logement ne peut pas etre vide ou blanche.")
    private String city;

    @Size(max = 255, message = "L'adresse du logement ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "L'adresse du logement ne peut pas etre vide ou blanche.")
    private String address;

    @DecimalMin(value = "0.00", inclusive = false, message = "Le prix du logement doit etre superieur a 0.")
    @Digits(integer = 8, fraction = 2, message = "Le prix du logement doit contenir au maximum 8 chiffres entiers et 2 decimales.")
    private BigDecimal price;

    private HousingType housingType;

    private Boolean available;
}
