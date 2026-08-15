package com.terangalink.backend.controller;

import com.terangalink.backend.requestDTO.CreateJobApplicationRequestDTO;
import com.terangalink.backend.responseDTO.JobApplicationResponseDTO;
import com.terangalink.backend.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping("/jobs/{jobPostId}/applications")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<JobApplicationResponseDTO> applyToJob(
            @PathVariable Long jobPostId,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam("cv") MultipartFile cv
    ) {
        CreateJobApplicationRequestDTO request = new CreateJobApplicationRequestDTO();
        request.setPhoneNumber(phoneNumber);
        request.setMessage(message);

        JobApplicationResponseDTO response = jobApplicationService.applyToJob(jobPostId, request, cv);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/job-applications/{id}")
                        .buildAndExpand(response.getId())
                        .toUri())
                .body(response);
    }

    @GetMapping("/jobs/{jobPostId}/applications/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<JobApplicationResponseDTO> getMyApplicationForJob(
            @PathVariable Long jobPostId
    ) {
        JobApplicationResponseDTO response = jobApplicationService.getMyApplicationForJob(jobPostId);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/job-applications/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<JobApplicationResponseDTO>> getMyApplications() {
        return ResponseEntity.ok(jobApplicationService.getMyApplications());
    }

    @GetMapping("/jobs/{jobPostId}/applications")
    @PreAuthorize("@jobPostSecurityService.canAccessJobPost(#jobPostId)")
    public ResponseEntity<List<JobApplicationResponseDTO>> getJobApplications(
            @PathVariable Long jobPostId
    ) {
        return ResponseEntity.ok(jobApplicationService.getJobApplications(jobPostId));
    }
}
