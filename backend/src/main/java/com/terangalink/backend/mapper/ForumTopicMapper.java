package com.terangalink.backend.mapper;

import com.terangalink.backend.entity.ForumTopic;
import com.terangalink.backend.requestDTO.CreateForumTopicRequestDTO;
import com.terangalink.backend.requestDTO.UpdateForumTopicRequestDTO;
import com.terangalink.backend.responseDTO.ForumTopicResponseDTO;
import org.springframework.stereotype.Component;

/*
FORUM MAPPER

Convertit les DTO en entités
et les entités en DTO.
*/

@Component
public class ForumTopicMapper {

    // Conversion DTO -> Entity
    public ForumTopic toEntity(CreateForumTopicRequestDTO dto) {

        ForumTopic forumTopic = new ForumTopic();

        forumTopic.setTitle(dto.getTitle());
        forumTopic.setContent(dto.getContent());
        forumTopic.setCategory(dto.getCategory());

        return forumTopic;
    }

    // Conversion Entity -> ResponseDTO
    public ForumTopicResponseDTO toResponseDto(ForumTopic forumTopic) {

        ForumTopicResponseDTO dto = new ForumTopicResponseDTO();

        dto.setId(forumTopic.getId());
        dto.setTitle(forumTopic.getTitle());
        dto.setContent(forumTopic.getContent());
        dto.setCategory(forumTopic.getCategory());

        if (forumTopic.getAuthor() != null) {

            dto.setAuthorId(forumTopic.getAuthor().getId());
            dto.setAuthorFirstName(forumTopic.getAuthor().getFirstName());
            dto.setAuthorLastName(forumTopic.getAuthor().getLastName());

        }

        dto.setViews(forumTopic.getViews());
        dto.setCreatedAt(forumTopic.getCreatedAt());
        dto.setUpdatedAt(forumTopic.getUpdatedAt());

        return dto;
    }

    // Mise à jour de l'entité
    public void updateEntity(
            ForumTopic forumTopic,
            UpdateForumTopicRequestDTO dto
    ) {

        if (dto.getTitle() != null) {
            forumTopic.setTitle(dto.getTitle());
        }

        if (dto.getContent() != null) {
            forumTopic.setContent(dto.getContent());
        }

        if (dto.getCategory() != null) {
            forumTopic.setCategory(dto.getCategory());
        }
    }

}