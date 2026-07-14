package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.ForumCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
CREATE FORUM TOPIC REQUEST DTO

Représente les données nécessaires
à la création d'un sujet.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class CreateForumTopicRequestDTO {

    // Titre du sujet
    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères.")
    private String title;

    // Contenu du sujet
    @NotBlank(message = "Le contenu est obligatoire.")
    @Size(max = 10000, message = "Le contenu ne doit pas dépasser 10000 caractères.")
    private String content;

    // Catégorie du sujet
    @NotNull(message = "La catégorie est obligatoire.")
    private ForumCategory category;
}