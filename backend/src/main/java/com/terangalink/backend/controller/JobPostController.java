package com.terangalink.backend.controller;

import com.terangalink.backend.enums.ContractType;
import com.terangalink.backend.requestDTO.CreateJobPostRequestDTO;
import com.terangalink.backend.requestDTO.UpdateJobPostRequestDTO;
import com.terangalink.backend.responseDTO.JobPostResponseDTO;
import com.terangalink.backend.service.JobPostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Set;

/*
JOB POST CONTROLLER

Expose les endpoints REST
pour les offres d'emploi.
*/

@RestController
@Validated
@RequestMapping("/api/jobs")
public class JobPostController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "title",
            "city",
            "companyName",
            "contractType",
            "salary",
            "available",
            "createdAt",
            "updatedAt"
    );

    private final JobPostService jobPostService;

    public JobPostController(JobPostService jobPostService) {
        this.jobPostService = jobPostService;
    }

    // Création d'une offre
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<JobPostResponseDTO> createJobPost(
            @RequestBody @Valid CreateJobPostRequestDTO request
    ) {

        JobPostResponseDTO response = jobPostService.createJobPost(request);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.getId())
                                .toUri())
                .body(response);
    }

    // Liste paginée des offres
    @GetMapping
    public ResponseEntity<Page<JobPostResponseDTO>> getAllJobPosts(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Le numero de page doit etre superieur ou egal a 0.")
            int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "La taille de page doit etre au moins 1.")
            @Max(value = 100, message = "La taille de page ne doit pas depasser 100.")
            int size,
            @RequestParam(defaultValue = "createdAt,desc")
            String[] sort
    ) {

        String sortField = sort.length > 0 ? sort[0] : "createdAt";

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException(
                    "Le champ de tri '" + sortField + "' n'est pas autorise.");
        }

        Sort.Direction direction =
                sort.length > 1 && "desc".equalsIgnoreCase(sort[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField)
        );

        return ResponseEntity.ok(
                jobPostService.getAllJobPosts(pageable)
        );
    }

    // Récupération d'une offre
    @GetMapping("/{id}")
    public ResponseEntity<JobPostResponseDTO> getJobPostById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                jobPostService.getJobPostById(id)
        );
    }

    // Recherche dynamique
    @GetMapping("/search")
    public ResponseEntity<Page<JobPostResponseDTO>> searchJobPosts(

            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            String companyName,

            @RequestParam(required = false)
            ContractType contractType,

            @RequestParam(required = false)
            BigDecimal salaryMin,

            @RequestParam(required = false)
            BigDecimal salaryMax,

            @RequestParam(required = false)
            Boolean available,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Le numero de page doit etre superieur ou egal a 0.")
            int page,

            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "La taille de page doit etre au moins 1.")
            @Max(value = 100, message = "La taille de page ne doit pas depasser 100.")
            int size,

            @RequestParam(defaultValue = "createdAt,desc")
            String[] sort
    ) {

        String sortField = sort.length > 0 ? sort[0] : "createdAt";

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException(
                    "Le champ de tri '" + sortField + "' n'est pas autorise.");
        }

        Sort.Direction direction =
                sort.length > 1 && "desc".equalsIgnoreCase(sort[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField)
        );

        return ResponseEntity.ok(
                jobPostService.searchJobPosts(
                        title,
                        city,
                        companyName,
                        contractType,
                        salaryMin,
                        salaryMax,
                        available,
                        pageable
                )
        );
    }

    // Modification d'une offre
    @PatchMapping("/{id}")
    @PreAuthorize("@jobPostSecurityService.canAccessJobPost(#id)")
    public ResponseEntity<JobPostResponseDTO> updateJobPost(
            @PathVariable Long id,
            @RequestBody @Valid UpdateJobPostRequestDTO request
    ) {

        return ResponseEntity.ok(
                jobPostService.updateJobPost(id, request)
        );
    }

    // Suppression logique d'une offre
    @DeleteMapping("/{id}")
    @PreAuthorize("@jobPostSecurityService.canAccessJobPost(#id)")
    public ResponseEntity<Void> deleteJobPost(
            @PathVariable Long id
    ) {

        jobPostService.deleteJobPost(id);

        return ResponseEntity.noContent().build();
    }
}
