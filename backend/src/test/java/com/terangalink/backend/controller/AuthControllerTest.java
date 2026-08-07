package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.exception.business.EmailAlreadyExistsException;
import com.terangalink.backend.exception.business.EmailNotVerifiedException;
import com.terangalink.backend.exception.business.ExpiredEmailVerificationTokenException;
import com.terangalink.backend.exception.business.ExpiredPasswordResetTokenException;
import com.terangalink.backend.exception.business.InvalidCurrentPasswordException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.InvalidEmailVerificationTokenException;
import com.terangalink.backend.exception.business.InvalidPasswordResetTokenException;
import com.terangalink.backend.exception.business.SamePasswordException;
import com.terangalink.backend.requestDTO.ChangePasswordRequestDTO;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.ForgotPasswordRequestDTO;
import com.terangalink.backend.requestDTO.LoginRequestDTO;
import com.terangalink.backend.requestDTO.ResetPasswordRequestDTO;
import com.terangalink.backend.requestDTO.VerifyEmailRequestDTO;
import com.terangalink.backend.responseDTO.AuthResponseDTO;
import com.terangalink.backend.responseDTO.MessageResponseDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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
    void register_shouldReturn201WithMessageResponse() throws Exception {
        CreateUserRequestDTO request = validCreateRequest();
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Compte cree avec succes. Verifiez votre adresse email.");

        when(authService.register(any(CreateUserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Compte cree avec succes. Verifiez votre adresse email."));
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
        CreateUserRequestDTO request = validCreateRequest();

        when(authService.register(any(CreateUserRequestDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Un utilisateur existe deja avec cet email."));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void login_shouldReturn200WithAuthResponse() throws Exception {
        LoginRequestDTO request = validLoginRequest();

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
        LoginRequestDTO request = validLoginRequest();

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
        LoginRequestDTO request = validLoginRequest();

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new EmailNotVerifiedException("Veuillez verifier votre adresse email avant de vous connecter."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("EMAIL_NOT_VERIFIED"));
    }

    @Test
    void getCurrentUser_shouldReturn200WithProfile() throws Exception {
        UserResponseDTO user = sampleUserResponse();

        when(authService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
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
        doThrow(new InvalidCurrentPasswordException("Le mot de passe actuel est incorrect."))
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
        doThrow(new SamePasswordException("Le nouveau mot de passe doit etre different."))
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
        VerifyEmailRequestDTO request = new VerifyEmailRequestDTO();
        request.setToken("123456");

        doNothing().when(authService).verifyEmail(any(VerifyEmailRequestDTO.class));

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void verifyEmail_shouldReturn400WhenTokenIsInvalid() throws Exception {
        VerifyEmailRequestDTO request = new VerifyEmailRequestDTO();
        request.setToken("123456");

        doThrow(new InvalidEmailVerificationTokenException("Le token de verification email est invalide."))
                .when(authService).verifyEmail(any(VerifyEmailRequestDTO.class));

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_EMAIL_VERIFICATION_TOKEN"));
    }

    @Test
    void verifyEmail_shouldReturn400WhenTokenIsExpired() throws Exception {
        VerifyEmailRequestDTO request = new VerifyEmailRequestDTO();
        request.setToken("123456");

        doThrow(new ExpiredEmailVerificationTokenException("Le token de verification email a expire."))
                .when(authService).verifyEmail(any(VerifyEmailRequestDTO.class));

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
        response.setUser(sampleUserResponse());
        return response;
    }

    private CreateUserRequestDTO validCreateRequest() {
        CreateUserRequestDTO request = new CreateUserRequestDTO();
        request.setFirstName("Alice");
        request.setLastName("Dupont");
        request.setEmail("alice@example.com");
        request.setPassword("Password1!");
        request.setUniversity("Sorbonne");
        request.setFieldOfStudy("Informatique");
        request.setCity("Paris");
        return request;
    }

    private LoginRequestDTO validLoginRequest() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("alice@example.com");
        request.setPassword("Password1!");
        return request;
    }

    private UserResponseDTO sampleUserResponse() {
        UserResponseDTO user = new UserResponseDTO();
        user.setId(1L);
        user.setFirstName("Alice");
        user.setLastName("Dupont");
        user.setEmail("alice@example.com");
        user.setUniversity("Sorbonne");
        user.setFieldOfStudy("Informatique");
        user.setCity("Paris");
        return user;
    }
}
