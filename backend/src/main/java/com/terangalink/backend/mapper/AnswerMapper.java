package com.terangalink.backend.mapper;

import com.terangalink.backend.entity.Answer;
import com.terangalink.backend.requestDTO.CreateAnswerRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAnswerRequestDTO;
import com.terangalink.backend.responseDTO.AnswerResponseDTO;
import org.springframework.stereotype.Component;

/*
ANSWER MAPPER

Convertit les DTO en entités
et les entités en DTO.
*/

@Component
public class AnswerMapper {

    // Conversion DTO -> Entity
    public Answer toEntity(CreateAnswerRequestDTO dto) {

        Answer answer = new Answer();

        answer.setContent(dto.getContent());

        return answer;
    }

    // Conversion Entity -> ResponseDTO
    public AnswerResponseDTO toResponseDto(Answer answer) {

        AnswerResponseDTO dto = new AnswerResponseDTO();

        dto.setId(answer.getId());
        dto.setContent(answer.getContent());

        if (answer.getForumTopic() != null) {
            dto.setForumTopicId(answer.getForumTopic().getId());
        }

        if (answer.getAuthor() != null) {
            dto.setAuthorId(answer.getAuthor().getId());
            dto.setAuthorFirstName(answer.getAuthor().getFirstName());
            dto.setAuthorLastName(answer.getAuthor().getLastName());
        }

        dto.setCreatedAt(answer.getCreatedAt());
        dto.setUpdatedAt(answer.getUpdatedAt());

        return dto;
    }

    // Mise à jour de l'entité
    public void updateEntity(
            Answer answer,
            UpdateAnswerRequestDTO dto
    ) {

        if (dto.getContent() != null) {
            answer.setContent(dto.getContent());
        }
    }
}
