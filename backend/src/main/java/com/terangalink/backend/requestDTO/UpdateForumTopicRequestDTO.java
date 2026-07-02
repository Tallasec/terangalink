package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terangalink.backend.enums.ForumCategory;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
UPDATE FORUM TOPIC REQUEST DTO

Représente les données utilisées
pour modifier un sujet.
*/

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)

public class UpdateForumTopicRequestDTO {

    // Titre du sujet
    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le titre ne peut pas être vide.")
    private String title;

    // Contenu du sujet
    @Size(max = 10000, message = "Le contenu ne doit pas dépasser 10000 caractères.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le contenu ne peut pas être vide.")
    private String content;

    // Catégorie du sujet
    private ForumCategory category;
}