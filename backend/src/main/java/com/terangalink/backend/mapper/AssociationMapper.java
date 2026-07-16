package com.terangalink.backend.mapper;

import com.terangalink.backend.entity.Association;
import com.terangalink.backend.requestDTO.CreateAssociationRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAssociationRequestDTO;
import com.terangalink.backend.responseDTO.AssociationResponseDTO;
import org.springframework.stereotype.Component;

/*
ASSOCIATION MAPPER

Convertit les DTO en entites
et les entites en DTO.
*/

@Component
public class AssociationMapper {

    // Conversion DTO -> Entity
    public Association toEntity(CreateAssociationRequestDTO dto) {

        Association association = new Association();

        association.setTitle(dto.getTitle());
        association.setDescription(dto.getDescription());
        association.setCity(dto.getCity());
        association.setAddress(dto.getAddress());
        association.setContactEmail(dto.getContactEmail());
        association.setPhone(dto.getPhone());
        association.setWebsite(dto.getWebsite());
        association.setLogoUrl(dto.getLogoUrl());
        association.setAssociationType(dto.getAssociationType());

        if (dto.getAvailable() != null) {
            association.setAvailable(dto.getAvailable());
        }

        return association;
    }

    // Conversion Entity -> ResponseDTO
    public AssociationResponseDTO toResponseDto(Association association) {

        AssociationResponseDTO dto = new AssociationResponseDTO();

        dto.setId(association.getId());
        dto.setTitle(association.getTitle());
        dto.setDescription(association.getDescription());
        dto.setCity(association.getCity());
        dto.setAddress(association.getAddress());
        dto.setContactEmail(association.getContactEmail());
        dto.setPhone(association.getPhone());
        dto.setWebsite(association.getWebsite());
        dto.setLogoUrl(association.getLogoUrl());
        dto.setAssociationType(association.getAssociationType());
        dto.setAvailable(association.isAvailable());

        if (association.getCreator() != null) {
            dto.setCreatorId(association.getCreator().getId());
            dto.setCreatorFirstName(association.getCreator().getFirstName());
            dto.setCreatorLastName(association.getCreator().getLastName());
        }

        dto.setCreatedAt(association.getCreatedAt());
        dto.setUpdatedAt(association.getUpdatedAt());

        return dto;
    }

    // Mise a jour de l'entite
    public void updateEntity(
            Association association,
            UpdateAssociationRequestDTO dto
    ) {

        if (dto.getTitle() != null) {
            association.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            association.setDescription(dto.getDescription());
        }

        if (dto.getCity() != null) {
            association.setCity(dto.getCity());
        }

        if (dto.getAddress() != null) {
            association.setAddress(dto.getAddress());
        }

        if (dto.getContactEmail() != null) {
            association.setContactEmail(dto.getContactEmail());
        }

        if (dto.getPhone() != null) {
            association.setPhone(dto.getPhone());
        }

        if (dto.getWebsite() != null) {
            association.setWebsite(dto.getWebsite());
        }

        if (dto.getLogoUrl() != null) {
            association.setLogoUrl(dto.getLogoUrl());
        }

        if (dto.getAssociationType() != null) {
            association.setAssociationType(dto.getAssociationType());
        }

        if (dto.getAvailable() != null) {
            association.setAvailable(dto.getAvailable());
        }
    }
}
