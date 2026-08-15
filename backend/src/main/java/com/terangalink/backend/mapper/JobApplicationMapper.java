package com.terangalink.backend.mapper;

import com.terangalink.backend.entity.JobApplication;
import com.terangalink.backend.requestDTO.CreateJobApplicationRequestDTO;
import com.terangalink.backend.responseDTO.JobApplicationResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {

    public JobApplication toEntity(CreateJobApplicationRequestDTO dto) {
        JobApplication application = new JobApplication();
        application.setPhoneNumber(dto.getPhoneNumber());
        application.setMessage(dto.getMessage());
        return application;
    }

    public JobApplicationResponseDTO toResponseDto(JobApplication application) {
        JobApplicationResponseDTO dto = new JobApplicationResponseDTO();
        dto.setId(application.getId());
        dto.setJobPostId(application.getJobPost() != null ? application.getJobPost().getId() : null);
        dto.setJobPostTitle(application.getJobPost() != null ? application.getJobPost().getTitle() : null);
        dto.setApplicantId(application.getApplicant() != null ? application.getApplicant().getId() : null);
        dto.setApplicantFirstName(application.getApplicant() != null ? application.getApplicant().getFirstName() : null);
        dto.setApplicantLastName(application.getApplicant() != null ? application.getApplicant().getLastName() : null);
        dto.setPhoneNumber(application.getPhoneNumber());
        dto.setMessage(application.getMessage());
        dto.setCvUrl(application.getCvUrl());
        dto.setCvOriginalFilename(application.getCvOriginalFilename());
        dto.setStatus(application.getStatus());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setUpdatedAt(application.getUpdatedAt());
        return dto;
    }
}
