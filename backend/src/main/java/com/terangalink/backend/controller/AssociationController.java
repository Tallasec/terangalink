package com.terangalink.backend.controller;

import com.terangalink.backend.enums.AssociationType;
import com.terangalink.backend.requestDTO.CreateAssociationRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAssociationRequestDTO;
import com.terangalink.backend.responseDTO.AssociationResponseDTO;
import com.terangalink.backend.service.AssociationService;
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

import java.util.Set;

/*
ASSOCIATION CONTROLLER

Expose les endpoints REST
pour les associations.
*/

@RestController
@Validated
@RequestMapping("/api/associations")
public class AssociationController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "title",
            "city",
            "associationType",
            "available",
            "createdAt",
            "updatedAt"
    );

    private final AssociationService associationService;

    public AssociationController(AssociationService associationService) {
        this.associationService = associationService;
    }

    // Creation d'une association
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<AssociationResponseDTO> createAssociation(
            @RequestBody @Valid CreateAssociationRequestDTO request
    ) {

        AssociationResponseDTO response = associationService.createAssociation(request);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.getId())
                                .toUri())
                .body(response);
    }

    // Liste paginee des associations
    @GetMapping
    public ResponseEntity<Page<AssociationResponseDTO>> getAllAssociations(
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
                associationService.getAllAssociations(pageable)
        );
    }

    // Recuperation d'une association
    @GetMapping("/{id}")
    public ResponseEntity<AssociationResponseDTO> getAssociationById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                associationService.getAssociationById(id)
        );
    }

    // Recherche dynamique
    @GetMapping("/search")
    public ResponseEntity<Page<AssociationResponseDTO>> searchAssociations(

            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            AssociationType associationType,

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
                associationService.searchAssociations(
                        title,
                        city,
                        associationType,
                        available,
                        pageable
                )
        );
    }

    // Modification d'une association
    @PatchMapping("/{id}")
    @PreAuthorize("@associationSecurityService.canAccessAssociation(#id)")
    public ResponseEntity<AssociationResponseDTO> updateAssociation(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAssociationRequestDTO request
    ) {

        return ResponseEntity.ok(
                associationService.updateAssociation(id, request)
        );
    }

    // Suppression logique d'une association
    @DeleteMapping("/{id}")
    @PreAuthorize("@associationSecurityService.canAccessAssociation(#id)")
    public ResponseEntity<Void> deleteAssociation(
            @PathVariable Long id
    ) {

        associationService.deleteAssociation(id);

        return ResponseEntity.noContent().build();
    }
}
