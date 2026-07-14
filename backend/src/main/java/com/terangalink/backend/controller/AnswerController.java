package com.terangalink.backend.controller;

import com.terangalink.backend.requestDTO.CreateAnswerRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAnswerRequestDTO;
import com.terangalink.backend.responseDTO.AnswerResponseDTO;
import com.terangalink.backend.service.AnswerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/*
ANSWER CONTROLLER

Expose les endpoints REST
pour les réponses du forum.
*/

@RestController
@RequestMapping("/api/forum")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    // Création d'une réponse
    @PostMapping("/topics/{topicId}/answers")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<AnswerResponseDTO> createAnswer(
            @PathVariable Long topicId,
            @RequestBody @Valid CreateAnswerRequestDTO request
    ) {

        AnswerResponseDTO response = answerService.createAnswer(topicId, request);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.getId())
                                .toUri())
                .body(response);
    }

    // Liste des réponses d'un sujet
    @GetMapping("/topics/{topicId}/answers")
    public ResponseEntity<List<AnswerResponseDTO>> getAnswersByForumTopic(
            @PathVariable Long topicId
    ) {

        return ResponseEntity.ok(
                answerService.getAnswersByForumTopic(topicId)
        );
    }

    // Récupération d'une réponse
    @GetMapping("/answers/{id}")
    public ResponseEntity<AnswerResponseDTO> getAnswerById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                answerService.getAnswerById(id)
        );
    }

    // Modification d'une réponse
    @PatchMapping("/answers/{id}")
    @PreAuthorize("@answerSecurityService.canAccessAnswer(#id)")
    public ResponseEntity<AnswerResponseDTO> updateAnswer(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAnswerRequestDTO request
    ) {

        return ResponseEntity.ok(
                answerService.updateAnswer(id, request)
        );
    }

    // Suppression logique d'une réponse
    @DeleteMapping("/answers/{id}")
    @PreAuthorize("@answerSecurityService.canAccessAnswer(#id)")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long id
    ) {

        answerService.deleteAnswer(id);

        return ResponseEntity.noContent().build();
    }
}
