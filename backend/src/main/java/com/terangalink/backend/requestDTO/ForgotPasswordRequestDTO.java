package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "L'adresse email est obligatoire.")
    @Email(message = "Le format de l'email est invalide.")
    @Size(max = 255, message = "L'adresse email ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^\\S+$", message = "L'adresse email ne doit contenir aucun espace.")
    private String email;
}
