package com.terangalink.backend.responseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
ANSWER RESPONSE DTO

Représente les informations
retournées au client.
*/

@Getter
@Setter
@NoArgsConstructor

public class AnswerResponseDTO {

    private Long id;

    private String content;

    private Long forumTopicId;

    private Long authorId;

    private String authorFirstName;

    private String authorLastName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
