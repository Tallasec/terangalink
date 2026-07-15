package com.terangalink.backend.mapper;

import com.terangalink.backend.entity.JobPost;
import com.terangalink.backend.requestDTO.CreateJobPostRequestDTO;
import com.terangalink.backend.requestDTO.UpdateJobPostRequestDTO;
import com.terangalink.backend.responseDTO.JobPostResponseDTO;
import org.springframework.stereotype.Component;

/*
JOB POST MAPPER

Convertit les DTO en entités
et les entités en DTO.
*/

@Component
public class JobPostMapper {

    // Conversion DTO -> Entity
    public JobPost toEntity(CreateJobPostRequestDTO dto) {

        JobPost jobPost = new JobPost();

        jobPost.setTitle(dto.getTitle());
        jobPost.setDescription(dto.getDescription());
        jobPost.setCompanyName(dto.getCompanyName());
        jobPost.setCity(dto.getCity());
        jobPost.setAddress(dto.getAddress());
        jobPost.setContractType(dto.getContractType());
        jobPost.setSalary(dto.getSalary());

        if (dto.getAvailable() != null) {
            jobPost.setAvailable(dto.getAvailable());
        }

        return jobPost;
    }

    // Conversion Entity -> ResponseDTO
    public JobPostResponseDTO toResponseDto(JobPost jobPost) {

        JobPostResponseDTO dto = new JobPostResponseDTO();

        dto.setId(jobPost.getId());
        dto.setTitle(jobPost.getTitle());
        dto.setDescription(jobPost.getDescription());
        dto.setCompanyName(jobPost.getCompanyName());
        dto.setCity(jobPost.getCity());
        dto.setAddress(jobPost.getAddress());
        dto.setContractType(jobPost.getContractType());
        dto.setSalary(jobPost.getSalary());
        dto.setAvailable(jobPost.isAvailable());

        if (jobPost.getOwner() != null) {
            dto.setOwnerId(jobPost.getOwner().getId());
            dto.setOwnerFirstName(jobPost.getOwner().getFirstName());
            dto.setOwnerLastName(jobPost.getOwner().getLastName());
        }

        dto.setCreatedAt(jobPost.getCreatedAt());
        dto.setUpdatedAt(jobPost.getUpdatedAt());

        return dto;
    }

    // Mise à jour de l'entité
    public void updateEntity(
            JobPost jobPost,
            UpdateJobPostRequestDTO dto
    ) {

        if (dto.getTitle() != null) {
            jobPost.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            jobPost.setDescription(dto.getDescription());
        }

        if (dto.getCompanyName() != null) {
            jobPost.setCompanyName(dto.getCompanyName());
        }

        if (dto.getCity() != null) {
            jobPost.setCity(dto.getCity());
        }

        if (dto.getAddress() != null) {
            jobPost.setAddress(dto.getAddress());
        }

        if (dto.getContractType() != null) {
            jobPost.setContractType(dto.getContractType());
        }

        if (dto.getSalary() != null) {
            jobPost.setSalary(dto.getSalary());
        }

        if (dto.getAvailable() != null) {
            jobPost.setAvailable(dto.getAvailable());
        }
    }
}
