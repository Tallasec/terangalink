package com.terangalink.backend.controller;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.UpdateUserRequestDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "firstName", "lastName", "email", "university", "fieldOfStudy", "city", "role", "createdAt");

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid CreateUserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest() // Récupère l'URL actuelle
                        .path("/{id}") // ajoute id sur l'URL actuelle récupèrer
                        .buildAndExpand(createdUser.getId()) // Récupère l'id qu'on va ajouter dns l'URL
                        .toUri()) // transforme l'URL (string) en uri (objet Java représentant une adresse de ressource)
                .body(createdUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getUsersPage(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Le numero de page doit etre superieur ou egal a 0.") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "La taille de page doit etre au moins 1.")
            @Max(value = 100, message = "La taille de page ne doit pas depasser 100.") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort.length > 0 ? sort[0] : "id";
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("Le champ de tri '" + sortField + "' n'est pas autorise.");
        }
        Sort.Direction direction = sort.length > 1 && "desc".equalsIgnoreCase(sort[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortField));
        return ResponseEntity.ok(userService.getAllUsers(pageRequest));
    }

    @PreAuthorize("@userSecurityService.canAccessUser(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }



    @PreAuthorize("@userSecurityService.canAccessUser(#id)")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> patchUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @RequestParam
            @Email(message = "Le format de l'email est invalide.")
            @Size(max = 255, message = "L'adresse email ne doit pas dépasser 255 caractères.")
            @Pattern(regexp = "^\\S+$", message = "L'adresse email ne doit contenir aucun espace.")
            String email) {
        UserResponseDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}