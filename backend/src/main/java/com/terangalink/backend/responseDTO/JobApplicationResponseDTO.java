package com.terangalink.backend.responseDTO;

import com.terangalink.backend.enums.JobApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class JobApplicationResponseDTO {

    private Long id;
    private Long jobPostId;
    private String jobPostTitle;
    private Long applicantId;
    private String applicantFirstName;
    private String applicantLastName;
    private String phoneNumber;
    private String message;
    private String cvUrl;
    private String cvOriginalFilename;
    private JobApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
