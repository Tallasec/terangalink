package com.terangalink.backend.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifyEmailRequestDTO {

    @NotBlank(message = "Le token de verification email est obligatoire.")
    @Size(max = 36, message = "Le token de verification email ne doit pas depasser 36 caracteres.")
    private String token;
}
