package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
UPDATE ANSWER REQUEST DTO

Représente les données utilisées
pour modifier une réponse.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class UpdateAnswerRequestDTO {

    // Contenu de la réponse
    @Size(max = 10000, message = "Le contenu ne doit pas dépasser 10000 caractères.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le contenu ne peut pas être vide.")
    private String content;
}
