package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.requestDTO.UpdateUserRequestDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.UserService;
import com.terangalink.backend.support.MethodSecurityTestConfig;
import com.terangalink.backend.support.UserTestFixtures;
import com.terangalink.backend.support.WithUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'autorisation du {@link UserController} : propriétaire + admin via {@code @PreAuthorize}.
 */
@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, MethodSecurityTestConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithUserPrincipal(id = 1L, role = Role.USER)
    void getUserById_shouldAllowUserToAccessOwnProfile() throws Exception {
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserPrincipal(id = 1L, role = Role.USER)
    void getUserById_shouldDenyUserAccessToOtherProfile() throws Exception {
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserPrincipal(id = 5L, role = Role.ADMIN)
    void getUserById_shouldAllowAdminToAccessAnyProfile() throws Exception {
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(99L);
        when(userService.getUserById(99L)).thenReturn(response);

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserPrincipal(id = 1L, role = Role.USER)
    void patchUser_shouldAllowUserToUpdateOwnProfile() throws Exception {
        UpdateUserRequestDTO request = UserTestFixtures.validUpdateRequest();
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);
        when(userService.updateUser(eq(1L), any(UpdateUserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserPrincipal(id = 1L, role = Role.USER)
    void patchUser_shouldDenyUserUpdateOfOtherProfile() throws Exception {
        UpdateUserRequestDTO request = UserTestFixtures.validUpdateRequest();

        mockMvc.perform(patch("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserPrincipal(id = 5L, role = Role.ADMIN)
    void patchUser_shouldAllowAdminToUpdateAnyProfile() throws Exception {
        UpdateUserRequestDTO request = UserTestFixtures.validUpdateRequest();
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(99L);
        when(userService.updateUser(eq(99L), any(UpdateUserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserPrincipal(id = 5L, role = Role.ADMIN)
    void deleteUser_shouldAllowAdmin() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserPrincipal(id = 1L, role = Role.USER)
    void deleteUser_shouldDenyUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_shouldDenyUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }
}
