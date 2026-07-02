package com.terangalink.backend.controller;

import com.terangalink.backend.enums.ForumCategory;
import com.terangalink.backend.requestDTO.CreateForumTopicRequestDTO;
import com.terangalink.backend.requestDTO.UpdateForumTopicRequestDTO;
import com.terangalink.backend.responseDTO.ForumTopicResponseDTO;
import com.terangalink.backend.service.ForumTopicService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;

/*
FORUM TOPIC CONTROLLER

Expose les endpoints REST
pour les sujets du forum.
*/

@RestController
@Validated
@RequestMapping("/api/forum/topics")
public class ForumTopicController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "title",
            "category",
            "views",
            "createdAt",
            "updatedAt"
    );

    private final ForumTopicService forumTopicService;

    public ForumTopicController(
            ForumTopicService forumTopicService
    ) {
        this.forumTopicService = forumTopicService;
    }

    // Création d'un sujet
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ForumTopicResponseDTO> createForumTopic(
            @RequestBody @Valid CreateForumTopicRequestDTO request
    ) {

        ForumTopicResponseDTO response =
                forumTopicService.createForumTopic(request);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.getId())
                                .toUri())
                .body(response);
    }

    // Liste paginée des sujets
    @GetMapping
    public ResponseEntity<Page<ForumTopicResponseDTO>> getAllForumTopics(

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Le numéro de page doit être supérieur ou égal à 0.")
            int page,

            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "La taille de page doit être au moins 1.")
            @Max(value = 100, message = "La taille de page ne doit pas dépasser 100.")
            int size,

            @RequestParam(defaultValue = "createdAt,desc")
            String[] sort
    ) {

        String sortField =
                sort.length > 0 ? sort[0] : "createdAt";

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException(
                    "Le champ de tri '" + sortField + "' n'est pas autorisé.");
        }

        Sort.Direction direction =
                sort.length > 1
                        && "desc".equalsIgnoreCase(sort[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        PageRequest pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(direction, sortField)
                );

        return ResponseEntity.ok(
                forumTopicService.getAllForumTopics(pageable)
        );
    }

    // Recherche dynamique
    @GetMapping("/search")
    public ResponseEntity<Page<ForumTopicResponseDTO>> searchForumTopics(

            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            ForumCategory category,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size,

            @RequestParam(defaultValue = "createdAt,desc")
            String[] sort
    ) {

        String sortField =
                sort.length > 0 ? sort[0] : "createdAt";

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException(
                    "Le champ de tri '" + sortField + "' n'est pas autorisé.");
        }

        Sort.Direction direction =
                sort.length > 1
                        && "desc".equalsIgnoreCase(sort[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        PageRequest pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(direction, sortField)
                );

        return ResponseEntity.ok(
                forumTopicService.searchForumTopics(
                        title,
                        category,
                        pageable
                )
        );
    }

    // Récupération d'un sujet
    @GetMapping("/{id}")
    public ResponseEntity<ForumTopicResponseDTO> getForumTopicById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                forumTopicService.getForumTopicById(id)
        );
    }

    // Modification d'un sujet
    @PatchMapping("/{id}")
    @PreAuthorize("@forumTopicSecurityService.canAccessForumTopic(#id)")
    public ResponseEntity<ForumTopicResponseDTO> updateForumTopic(

            @PathVariable Long id,

            @RequestBody
            @Valid
            UpdateForumTopicRequestDTO request
    ) {

        return ResponseEntity.ok(
                forumTopicService.updateForumTopic(id, request)
        );
    }

    // Suppression logique d'un sujet
    @DeleteMapping("/{id}")
    @PreAuthorize("@forumTopicSecurityService.canAccessForumTopic(#id)")
    public ResponseEntity<Void> deleteForumTopic(
            @PathVariable Long id
    ) {

        forumTopicService.deleteForumTopic(id);

        return ResponseEntity.noContent().build();
    }

}