package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.exception.business.EmailAlreadyExistsException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.LoginRequestDTO;
import com.terangalink.backend.responseDTO.AuthResponseDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.service.AuthService;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.support.AuthTestFixtures;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void register_shouldReturn201WithAuthResponse() throws Exception {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();
        AuthResponseDTO response = buildAuthResponse();

        when(authService.register(any(CreateUserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(86400))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value(UserTestFixtures.NORMALIZED_EMAIL))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void register_shouldReturn400WhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void register_shouldReturn409WhenEmailAlreadyExists() throws Exception {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();

        when(authService.register(any(CreateUserRequestDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Un utilisateur existe déjà avec cet email."));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void login_shouldReturn200WithAuthResponse() throws Exception {
        LoginRequestDTO request = AuthTestFixtures.validLoginRequest();

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(buildAuthResponse());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_shouldReturn400WhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void login_shouldReturn401WhenCredentialsAreInvalid() throws Exception {
        LoginRequestDTO request = AuthTestFixtures.validLoginRequest();

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new InvalidCredentialsException("Identifiants invalides."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }

    @Test
    void getCurrentUser_shouldReturn200WithProfile() throws Exception {
        UserResponseDTO user = UserTestFixtures.sampleUserResponse(1L);

        when(authService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(UserTestFixtures.NORMALIZED_EMAIL));
    }

    private AuthResponseDTO buildAuthResponse() {
        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken("jwt-token");
        response.setTokenType("Bearer");
        response.setExpiresIn(86_400L);
        response.setUser(UserTestFixtures.sampleUserResponse(1L));
        return response;
    }
}
