package com.terangalink.backend.mapper;

import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.requestDTO.CreateHousingRequestDTO;
import com.terangalink.backend.requestDTO.UpdateHousingRequestDTO;
import com.terangalink.backend.responseDTO.HousingImageResponseDTO;
import com.terangalink.backend.responseDTO.HousingResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HousingMapper {

    public HousingPost toEntity(CreateHousingRequestDTO dto) {
        HousingPost housingPost = new HousingPost();
        housingPost.setTitle(dto.getTitle());
        housingPost.setDescription(dto.getDescription());
        housingPost.setCity(dto.getCity());
        housingPost.setAddress(dto.getAddress());
        housingPost.setPrice(dto.getPrice());
        housingPost.setHousingType(dto.getHousingType());
        if (dto.getAvailable() != null) {
            housingPost.setAvailable(dto.getAvailable());
        }
        return housingPost;
    }

    public HousingResponseDTO toResponseDto(HousingPost housingPost) {
        HousingResponseDTO dto = new HousingResponseDTO();
        dto.setId(housingPost.getId());
        dto.setTitle(housingPost.getTitle());
        dto.setDescription(housingPost.getDescription());
        dto.setCity(housingPost.getCity());
        dto.setAddress(housingPost.getAddress());
        dto.setPrice(housingPost.getPrice());
        dto.setHousingType(housingPost.getHousingType());
        dto.setAvailable(housingPost.isAvailable());
        dto.setOwnerId(housingPost.getOwner() != null ? housingPost.getOwner().getId() : null);
        dto.setCreatedAt(housingPost.getCreatedAt());
        dto.setUpdatedAt(housingPost.getUpdatedAt());
        if (housingPost.getOwner() != null) {
            dto.setOwnerId(housingPost.getOwner().getId());
            dto.setOwnerFirstName(housingPost.getOwner().getFirstName());
            dto.setOwnerLastName(housingPost.getOwner().getLastName());
        }
        if (housingPost.getImages() != null) {
            List<HousingImageResponseDTO> images = housingPost.getImages().stream()
                    .map(image -> {
                        HousingImageResponseDTO imageDto = new HousingImageResponseDTO();
                        imageDto.setId(image.getId());
                        imageDto.setImageUrl(image.getImageUrl());
                        return imageDto;
                    })
                    .toList();
            dto.setImages(images);
        }
        return dto;
    }

    public void updateEntity(HousingPost housingPost,  UpdateHousingRequestDTO dto) {
        if (dto.getTitle() != null) {
            housingPost.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            housingPost.setDescription(dto.getDescription());
        }
        if (dto.getCity() != null) {
            housingPost.setCity(dto.getCity());
        }
        if (dto.getAddress() != null) {
            housingPost.setAddress(dto.getAddress());
        }
        if (dto.getPrice() != null) {
            housingPost.setPrice(dto.getPrice());
        }
        if (dto.getHousingType() != null) {
            housingPost.setHousingType(dto.getHousingType());
        }
        if (dto.getAvailable() != null) {
            housingPost.setAvailable(dto.getAvailable());
        }

    }
}
