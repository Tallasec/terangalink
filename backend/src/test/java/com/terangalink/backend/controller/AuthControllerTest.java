package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.exception.business.EmailAlreadyExistsException;
import com.terangalink.backend.exception.business.EmailNotVerifiedException;
import com.terangalink.backend.exception.business.ExpiredPasswordResetTokenException;
import com.terangalink.backend.exception.business.InvalidCurrentPasswordException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.ExpiredEmailVerificationTokenException;
import com.terangalink.backend.exception.business.InvalidPasswordResetTokenException;
import com.terangalink.backend.exception.business.SamePasswordException;
import com.terangalink.backend.requestDTO.ChangePasswordRequestDTO;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.ForgotPasswordRequestDTO;
import com.terangalink.backend.requestDTO.LoginRequestDTO;
import com.terangalink.backend.requestDTO.ResetPasswordRequestDTO;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    void login_shouldReturn403WhenEmailIsNotVerified() throws Exception {
        LoginRequestDTO request = AuthTestFixtures.validLoginRequest();

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new EmailNotVerifiedException(
                        "Veuillez vérifier votre adresse email avant de vous connecter."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("EMAIL_NOT_VERIFIED"));
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

    @Test
    void changePassword_shouldReturn204() throws Exception {
        ChangePasswordRequestDTO request = changePasswordRequest();
        doNothing().when(authService).changePassword(any(ChangePasswordRequestDTO.class));

        mockMvc.perform(patch("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePassword_shouldReturn400WhenNewPasswordIsInvalid() throws Exception {
        ChangePasswordRequestDTO request = changePasswordRequest();
        request.setNewPassword("weak");

        mockMvc.perform(patch("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.newPassword").exists());
    }

    @Test
    void changePassword_shouldReturn401WhenCurrentPasswordIsInvalid() throws Exception {
        ChangePasswordRequestDTO request = changePasswordRequest();
        org.mockito.Mockito.doThrow(new InvalidCurrentPasswordException("Le mot de passe actuel est incorrect."))
                .when(authService).changePassword(any(ChangePasswordRequestDTO.class));

        mockMvc.perform(patch("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CURRENT_PASSWORD"));
    }

    @Test
    void changePassword_shouldReturn400WhenPasswordIsUnchanged() throws Exception {
        ChangePasswordRequestDTO request = changePasswordRequest();
        org.mockito.Mockito.doThrow(new SamePasswordException("Le nouveau mot de passe doit etre different."))
                .when(authService).changePassword(any(ChangePasswordRequestDTO.class));

        mockMvc.perform(patch("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("SAME_PASSWORD"));
    }

    @Test
    void forgotPassword_shouldReturn204() throws Exception {
        ForgotPasswordRequestDTO request = forgotPasswordRequest("alice@example.com");
        doNothing().when(authService).forgotPassword(any(ForgotPasswordRequestDTO.class));

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void forgotPassword_shouldReturn204ForUnknownEmail() throws Exception {
        ForgotPasswordRequestDTO request = forgotPasswordRequest("unknown@example.com");
        doNothing().when(authService).forgotPassword(any(ForgotPasswordRequestDTO.class));

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void forgotPassword_shouldReturn400WhenEmailIsInvalid() throws Exception {
        ForgotPasswordRequestDTO request = forgotPasswordRequest("invalid-email");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void resetPassword_shouldReturn204() throws Exception {
        ResetPasswordRequestDTO request = resetPasswordRequest();
        doNothing().when(authService).resetPassword(any(ResetPasswordRequestDTO.class));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void resetPassword_shouldReturn400WhenPasswordIsInvalid() throws Exception {
        ResetPasswordRequestDTO request = resetPasswordRequest();
        request.setNewPassword("weak");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.newPassword").exists());
    }

    @Test
    void resetPassword_shouldReturn400WhenTokenIsInvalid() throws Exception {
        ResetPasswordRequestDTO request = resetPasswordRequest();
        doThrow(new InvalidPasswordResetTokenException("Token invalide."))
                .when(authService).resetPassword(any(ResetPasswordRequestDTO.class));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_PASSWORD_RESET_TOKEN"));
    }

    @Test
    void resetPassword_shouldReturn400WhenTokenIsExpired() throws Exception {
        ResetPasswordRequestDTO request = resetPasswordRequest();
        doThrow(new ExpiredPasswordResetTokenException("Token expire."))
                .when(authService).resetPassword(any(ResetPasswordRequestDTO.class));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("EXPIRED_PASSWORD_RESET_TOKEN"));
    }

    @Test
    void verifyEmail_shouldReturn204() throws Exception {
        mockMvc.perform(get("/api/auth/verify-email")
                        .param("token", "550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(status().isNoContent());
    }

    @Test
    void verifyEmail_shouldReturn400WhenTokenIsInvalid() throws Exception {
        doThrow(new com.terangalink.backend.exception.business.InvalidEmailVerificationTokenException(
                "Le token de verification email est invalide."))
                .when(authService).verifyEmail(any(com.terangalink.backend.requestDTO.VerifyEmailRequestDTO.class));

        mockMvc.perform(get("/api/auth/verify-email")
                        .param("token", "invalid-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_EMAIL_VERIFICATION_TOKEN"));
    }

    @Test
    void verifyEmail_shouldReturn400WhenTokenIsExpired() throws Exception {
        doThrow(new ExpiredEmailVerificationTokenException("Le token de verification email a expire."))
                .when(authService).verifyEmail(any(com.terangalink.backend.requestDTO.VerifyEmailRequestDTO.class));

        mockMvc.perform(get("/api/auth/verify-email")
                        .param("token", "550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("EXPIRED_EMAIL_VERIFICATION_TOKEN"));
    }

    private ChangePasswordRequestDTO changePasswordRequest() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword("Password1!");
        request.setNewPassword("NewPassword2!");
        return request;
    }

    private ForgotPasswordRequestDTO forgotPasswordRequest(String email) {
        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO();
        request.setEmail(email);
        return request;
    }

    private ResetPasswordRequestDTO resetPasswordRequest() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setToken("550e8400-e29b-41d4-a716-446655440000");
        request.setNewPassword("NewPassword2!");
        return request;
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
