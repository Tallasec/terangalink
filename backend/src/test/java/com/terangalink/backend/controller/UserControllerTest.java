package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.exception.business.EmailAlreadyExistsException;
import com.terangalink.backend.exception.business.InvalidUserPatchException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.UpdateUserRequestDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.service.UserService;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests du {@link UserController} via MockMvc : contrat HTTP, validation des DTO,
 * format JSON des réponses et propagation des erreurs métier.
 * <p>
 * Utile pour garantir que l'API REST respecte les codes de statut et le schéma
 * attendu par les clients, sans démarrer toute l'application.
 */
@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_shouldReturn201WithLocationAndBody() throws Exception {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(42L);

        when(userService.createUser(any(CreateUserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/users/42")))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Dupont"))
                .andExpect(jsonPath("$.email").value(UserTestFixtures.NORMALIZED_EMAIL))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createUser_shouldReturn400WhenValidationFails() throws Exception {
        CreateUserRequestDTO request = new CreateUserRequestDTO();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Les données de la requête sont invalides."))
                .andExpect(jsonPath("$.details.firstName").exists())
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.password").exists());
    }

    @Test
    void createUser_shouldReturn409WhenEmailAlreadyExists() throws Exception {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();

        when(userService.createUser(any(CreateUserRequestDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Un utilisateur existe déjà avec cet email."));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void getUsersPage_shouldReturn200WithPagedJson() throws Exception {
        UserResponseDTO user = UserTestFixtures.sampleUserResponse(1L);
        when(userService.getAllUsers(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value(UserTestFixtures.NORMALIZED_EMAIL))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void getUsersPage_shouldReturn400WhenSortFieldIsInvalid() throws Exception {
        mockMvc.perform(get("/api/users").param("sort", "unknownField,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("INVALID_REQUEST_PARAMETER"))
                .andExpect(jsonPath("$.message").value(containsString("n'est pas autorise")));
    }

    @Test
    void getUserById_shouldReturn200() throws Exception {
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.university").value("Sorbonne"));
    }

    @Test
    void getUserById_shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new UserNotFoundException("Utilisateur introuvable avec l'id : 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"));
    }

    @Test
    void getUserByEmail_shouldReturn200() throws Exception {
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);
        when(userService.getUserByEmail(UserTestFixtures.VALID_EMAIL)).thenReturn(response);

        mockMvc.perform(get("/api/users/search").param("email", UserTestFixtures.VALID_EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(UserTestFixtures.NORMALIZED_EMAIL));
    }

    @Test
    void getUserByEmail_shouldReturn400WhenEmailFormatIsInvalid() throws Exception {
        mockMvc.perform(get("/api/users/search").param("email", "not-an-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("CONSTRAINT_VIOLATION"));
    }

    @Test
    void patchUser_shouldReturn200WithUpdatedBody() throws Exception {
        UpdateUserRequestDTO request = UserTestFixtures.validUpdateRequest();
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);
        response.setFirstName("Alicia");

        when(userService.updateUser(eq(1L), any(UpdateUserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alicia"));
    }

    @Test
    void patchUser_shouldReturn400WhenPasswordIsTooWeak() throws Exception {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setPassword("weak");

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.password").exists());
    }

    @Test
    void patchUser_shouldReturn400WhenPatchIsEmpty() throws Exception {
        when(userService.updateUser(eq(1L), any(UpdateUserRequestDTO.class)))
                .thenThrow(new InvalidUserPatchException(
                        "Le corps PATCH ne peut pas etre vide. Fournissez au moins un champ a modifier."));

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_PATCH_REQUEST"));
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_shouldReturn404WhenUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("Utilisateur introuvable avec l'id : 1"))
                .when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"));
    }

    @Test
    void getUsersPage_shouldReturn400WhenPageIsNegative() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("CONSTRAINT_VIOLATION"));
    }

    @Test
    void getUsersPage_shouldReturn400WhenSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("CONSTRAINT_VIOLATION"));
    }

    @Test
    void getUserByEmail_shouldReturn400WhenEmailContainsSpaces() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("email", " alice@example.com "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }



}
