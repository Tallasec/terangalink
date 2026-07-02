package com.terangalink.backend.responseDTO;

import com.terangalink.backend.enums.ForumCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
FORUM TOPIC RESPONSE DTO

Représente les informations
retournées au client.
*/

@Getter
@Setter
@NoArgsConstructor

public class ForumTopicResponseDTO {

    private Long id;

    private String title;

    private String content;

    private ForumCategory category;

    private Long authorId;

    private String authorFirstName;

    private String authorLastName;

    private Long views;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}