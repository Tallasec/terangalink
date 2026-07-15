package com.terangalink.backend.mapper;

import com.terangalink.backend.entity.StudyGroup;
import com.terangalink.backend.requestDTO.CreateStudyGroupRequestDTO;
import com.terangalink.backend.requestDTO.UpdateStudyGroupRequestDTO;
import com.terangalink.backend.responseDTO.StudyGroupResponseDTO;
import org.springframework.stereotype.Component;

/*
STUDY GROUP MAPPER

Convertit les DTO en entités
et les entités en DTO.
*/

@Component
public class StudyGroupMapper {

    // Conversion DTO -> Entity
    public StudyGroup toEntity(CreateStudyGroupRequestDTO dto) {

        StudyGroup studyGroup = new StudyGroup();

        studyGroup.setTitle(dto.getTitle());
        studyGroup.setSubject(dto.getSubject());
        studyGroup.setDescription(dto.getDescription());
        studyGroup.setCity(dto.getCity());
        studyGroup.setLocation(dto.getLocation());
        studyGroup.setMeetingType(dto.getMeetingType());
        studyGroup.setMeetingDate(dto.getMeetingDate());
        studyGroup.setMaxMembers(dto.getMaxMembers());

        if (dto.getAvailable() != null) {
            studyGroup.setAvailable(dto.getAvailable());
        }

        return studyGroup;
    }

    // Conversion Entity -> ResponseDTO
    public StudyGroupResponseDTO toResponseDto(StudyGroup studyGroup) {

        StudyGroupResponseDTO dto = new StudyGroupResponseDTO();

        dto.setId(studyGroup.getId());
        dto.setTitle(studyGroup.getTitle());
        dto.setSubject(studyGroup.getSubject());
        dto.setDescription(studyGroup.getDescription());
        dto.setCity(studyGroup.getCity());
        dto.setLocation(studyGroup.getLocation());
        dto.setMeetingType(studyGroup.getMeetingType());
        dto.setMeetingDate(studyGroup.getMeetingDate());
        dto.setMaxMembers(studyGroup.getMaxMembers());
        dto.setAvailable(studyGroup.isAvailable());

        if (studyGroup.getCreator() != null) {
            dto.setCreatorId(studyGroup.getCreator().getId());
            dto.setCreatorFirstName(studyGroup.getCreator().getFirstName());
            dto.setCreatorLastName(studyGroup.getCreator().getLastName());
        }

        dto.setCreatedAt(studyGroup.getCreatedAt());
        dto.setUpdatedAt(studyGroup.getUpdatedAt());

        return dto;
    }

    // Mise à jour de l'entité
    public void updateEntity(
            StudyGroup studyGroup,
            UpdateStudyGroupRequestDTO dto
    ) {

        if (dto.getTitle() != null) {
            studyGroup.setTitle(dto.getTitle());
        }

        if (dto.getSubject() != null) {
            studyGroup.setSubject(dto.getSubject());
        }

        if (dto.getDescription() != null) {
            studyGroup.setDescription(dto.getDescription());
        }

        if (dto.getCity() != null) {
            studyGroup.setCity(dto.getCity());
        }

        if (dto.getLocation() != null) {
            studyGroup.setLocation(dto.getLocation());
        }

        if (dto.getMeetingType() != null) {
            studyGroup.setMeetingType(dto.getMeetingType());
        }

        if (dto.getMeetingDate() != null) {
            studyGroup.setMeetingDate(dto.getMeetingDate());
        }

        if (dto.getMaxMembers() != null) {
            studyGroup.setMaxMembers(dto.getMaxMembers());
        }

        if (dto.getAvailable() != null) {
            studyGroup.setAvailable(dto.getAvailable());
        }
    }
}
