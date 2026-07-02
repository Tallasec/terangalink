package com.terangalink.backend.responseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
ANSWER RESPONSE DTO

Représente une réponse
retournée au client.
*/

@Getter
@Setter
@NoArgsConstructor

public class AnswerResponseDTO {

    // Identifiant
    private Long id;

    // Contenu
    private String content;

    // Identifiant du sujet
    private Long forumTopicId;

    // Auteur
    private Long authorId;

    private String authorFirstName;

    private String authorLastName;

    // Dates
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}