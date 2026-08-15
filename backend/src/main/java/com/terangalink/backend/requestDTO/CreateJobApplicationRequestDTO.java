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
public class CreateJobApplicationRequestDTO {

    @NotBlank(message = "Le numero de telephone est obligatoire.")
    @Size(max = 20, message = "Le numero de telephone ne doit pas depasser 20 caracteres.")
    private String phoneNumber;

    @Size(max = 2000, message = "Le message ne doit pas depasser 2000 caracteres.")
    private String message;
}
