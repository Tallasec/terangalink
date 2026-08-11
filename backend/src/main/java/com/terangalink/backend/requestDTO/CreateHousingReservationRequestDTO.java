package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateHousingReservationRequestDTO {

    @NotBlank(message = "Le numero de telephone est obligatoire.")
    @Size(max = 20, message = "Le numero de telephone ne doit pas depasser 20 caracteres.")
    private String phoneNumber;

    @NotBlank(message = "Le message est obligatoire.")
    @Size(max = 1000, message = "Le message ne doit pas depasser 1000 caracteres.")
    private String message;
}
