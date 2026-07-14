package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
CREATE ANSWER REQUEST DTO

Représente les données nécessaires
à la création d'une réponse.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class CreateAnswerRequestDTO {

    // Contenu de la réponse
    @NotBlank(message = "Le contenu est obligatoire.")
    @Size(max = 10000, message = "Le contenu ne doit pas dépasser 10000 caractères.")
    private String content;
}
