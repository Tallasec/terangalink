package com.terangalink.backend.controller;

import com.terangalink.backend.enums.MeetingType;
import com.terangalink.backend.requestDTO.CreateStudyGroupRequestDTO;
import com.terangalink.backend.requestDTO.UpdateStudyGroupRequestDTO;
import com.terangalink.backend.responseDTO.StudyGroupResponseDTO;
import com.terangalink.backend.service.StudyGroupService;
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

import java.time.LocalDateTime;
import java.util.Set;

/*
STUDY GROUP CONTROLLER

Expose les endpoints REST
pour les groupes de révision.
*/

@RestController
@Validated
@RequestMapping("/api/study-groups")
public class StudyGroupController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "title",
            "subject",
            "city",
            "meetingType",
            "meetingDate",
            "available",
            "createdAt",
            "updatedAt"
    );

    private final StudyGroupService studyGroupService;

    public StudyGroupController(StudyGroupService studyGroupService) {
        this.studyGroupService = studyGroupService;
    }

    // Création d'un groupe
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StudyGroupResponseDTO> createStudyGroup(
            @RequestBody @Valid CreateStudyGroupRequestDTO request
    ) {

        StudyGroupResponseDTO response = studyGroupService.createStudyGroup(request);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.getId())
                                .toUri())
                .body(response);
    }

    // Liste paginée des groupes
    @GetMapping
    public ResponseEntity<Page<StudyGroupResponseDTO>> getAllStudyGroups(
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
                studyGroupService.getAllStudyGroups(pageable)
        );
    }

    // Récupération d'un groupe
    @GetMapping("/{id}")
    public ResponseEntity<StudyGroupResponseDTO> getStudyGroupById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                studyGroupService.getStudyGroupById(id)
        );
    }

    // Recherche dynamique
    @GetMapping("/search")
    public ResponseEntity<Page<StudyGroupResponseDTO>> searchStudyGroups(

            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            String subject,

            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            MeetingType meetingType,

            @RequestParam(required = false)
            Boolean available,

            @RequestParam(required = false)
            LocalDateTime meetingDate,

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
                studyGroupService.searchStudyGroups(
                        title,
                        subject,
                        city,
                        meetingType,
                        available,
                        meetingDate,
                        pageable
                )
        );
    }

    // Modification d'un groupe
    @PatchMapping("/{id}")
    @PreAuthorize("@studyGroupSecurityService.canAccessStudyGroup(#id)")
    public ResponseEntity<StudyGroupResponseDTO> updateStudyGroup(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStudyGroupRequestDTO request
    ) {

        return ResponseEntity.ok(
                studyGroupService.updateStudyGroup(id, request)
        );
    }

    // Suppression logique d'un groupe
    @DeleteMapping("/{id}")
    @PreAuthorize("@studyGroupSecurityService.canAccessStudyGroup(#id)")
    public ResponseEntity<Void> deleteStudyGroup(
            @PathVariable Long id
    ) {

        studyGroupService.deleteStudyGroup(id);

        return ResponseEntity.noContent().build();
    }
}
