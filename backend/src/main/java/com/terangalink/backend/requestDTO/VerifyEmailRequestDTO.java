package com.terangalink.backend.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifyEmailRequestDTO {

    @NotBlank(message = "Le token de verification email est obligatoire.")
    @Size(min = 6, max = 6, message = "Le token de verification email doit contenir 6 caracteres.")
    @Pattern(regexp = "^\\d{6}$", message = "Le token de verification email doit contenir 6 chiffres.")
    private String token;
}
