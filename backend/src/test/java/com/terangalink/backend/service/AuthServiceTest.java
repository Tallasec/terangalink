package com.terangalink.backend.service;

import com.terangalink.backend.config.JwtProperties;
import com.terangalink.backend.entity.PasswordResetToken;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.exception.business.EmailAlreadyVerifiedException;
import com.terangalink.backend.exception.business.EmailNotVerifiedException;
import com.terangalink.backend.exception.business.ExpiredPasswordResetTokenException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.InvalidCurrentPasswordException;
import com.terangalink.backend.exception.business.InvalidPasswordResetTokenException;
import com.terangalink.backend.exception.business.SamePasswordException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.UserMapper;
import com.terangalink.backend.repository.PasswordResetTokenRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.ChangePasswordRequestDTO;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.ForgotPasswordRequestDTO;
import com.terangalink.backend.requestDTO.LoginRequestDTO;
import com.terangalink.backend.requestDTO.ResendVerificationEmailRequestDTO;
import com.terangalink.backend.requestDTO.ResetPasswordRequestDTO;
import com.terangalink.backend.requestDTO.VerifyEmailRequestDTO;
import com.terangalink.backend.responseDTO.AuthResponseDTO;
import com.terangalink.backend.responseDTO.MessageResponseDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.support.AuthTestFixtures;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserService userService;
    @Mock private EmailVerificationService emailVerificationService;
    @Mock private JwtService jwtService;
    @Mock private JwtProperties jwtProperties;
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailNormalizer emailNormalizer;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_shouldCreateUserAndIssueVerificationCode() {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();
        UserResponseDTO createdUser = UserTestFixtures.sampleUserResponse(1L);
        User savedUser = UserTestFixtures.sampleUser(1L);

        when(userService.createUser(request)).thenReturn(createdUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        MessageResponseDTO response = authService.register(request);

        assertThat(response.getMessage()).isEqualTo("Compte créé avec succès. Vérifiez votre adresse email.");
        verify(emailVerificationService).issueVerificationCode(savedUser);
    }

    @Test
    void login_shouldReturnAuthResponseWhenCredentialsAreValid() {
        LoginRequestDTO request = AuthTestFixtures.validLoginRequest();
        User user = UserTestFixtures.sampleUser(1L);
        UserResponseDTO userResponse = UserTestFixtures.sampleUserResponse(1L);

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(UserTestFixtures.VALID_PASSWORD, user.getPassword())).thenReturn(true);
        when(jwtProperties.getExpirationMs()).thenReturn(86_400_000L);
        when(jwtService.generateToken(any(UserPrincipal.class))).thenReturn("jwt-token");
        when(userMapper.toResponseDto(user)).thenReturn(userResponse);

        user.setEmailVerified(true);

        AuthResponseDTO response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getUser()).isEqualTo(userResponse);
    }

    @Test
    void login_shouldThrowWhenEmailIsNotVerified() {
        LoginRequestDTO request = AuthTestFixtures.validLoginRequest();
        User user = UserTestFixtures.sampleUser(1L);

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(UserTestFixtures.VALID_PASSWORD, user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(EmailNotVerifiedException.class);
    }

    @Test
    void getCurrentUser_shouldReturnMappedProfile() {
        User user = UserTestFixtures.sampleUser(1L);
        UserPrincipal principal = UserPrincipal.from(user);
        UserResponseDTO userResponse = UserTestFixtures.sampleUserResponse(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(userResponse);

        assertThat(authService.getCurrentUser()).isEqualTo(userResponse);
    }

    @Test
    void changePassword_shouldEncodeAndSaveNewPassword() {
        User user = UserTestFixtures.sampleUser(1L);
        UserPrincipal principal = UserPrincipal.from(user);
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword("Password1!");
        request.setNewPassword("NewPassword2!");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), "encoded-password")).thenReturn(true);
        when(passwordEncoder.matches(request.getNewPassword(), "encoded-password")).thenReturn(false);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("new-encoded-password");

        authService.changePassword(request);

        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrowWhenCurrentPasswordIsInvalid() {
        User user = UserTestFixtures.sampleUser(1L);
        UserPrincipal principal = UserPrincipal.from(user);
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword("WrongPassword1!");
        request.setNewPassword("NewPassword2!");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(request))
                .isInstanceOf(InvalidCurrentPasswordException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void forgotPassword_shouldCreateTokenWhenUserExists() {
        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO();
        request.setEmail(UserTestFixtures.VALID_EMAIL);
        User user = UserTestFixtures.sampleUser(1L);

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(Optional.of(user));

        authService.forgotPassword(request);

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_shouldThrowWhenTokenDoesNotExist() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setToken("missing-token");
        request.setNewPassword("NewPassword2!");

        when(passwordResetTokenRepository.findByToken(request.getToken())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(InvalidPasswordResetTokenException.class);
    }

    @Test
    void resetPassword_shouldThrowWhenTokenIsExpired() {
        User user = UserTestFixtures.sampleUser(1L);
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("token");
        token.setUser(user);
        token.setExpiresAt(java.time.LocalDateTime.now().minusMinutes(1));
        token.setUsed(false);

        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setToken("token");
        request.setNewPassword("NewPassword2!");

        when(passwordResetTokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(ExpiredPasswordResetTokenException.class);
    }

    @Test
    void verifyEmail_shouldDelegateToVerificationService() {
        VerifyEmailRequestDTO request = new VerifyEmailRequestDTO();
        request.setToken("550e8400-e29b-41d4-a716-446655440000");

        authService.verifyEmail(request);

        verify(emailVerificationService).verifyEmail(request);
    }

    @Test
    void resendVerificationEmail_shouldResendForExistingUnverifiedUser() {
        ResendVerificationEmailRequestDTO request = new ResendVerificationEmailRequestDTO();
        request.setEmail(UserTestFixtures.VALID_EMAIL);
        User user = UserTestFixtures.sampleUser(1L);
        user.setEmailVerified(false);

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(Optional.of(user));

        MessageResponseDTO response = authService.resendVerificationEmail(request);

        assertThat(response.getMessage()).isEqualTo("Un nouveau code a été envoyé.");
        verify(emailVerificationService).resendVerificationCode(user);
    }

    @Test
    void resendVerificationEmail_shouldRejectAlreadyVerifiedUser() {
        ResendVerificationEmailRequestDTO request = new ResendVerificationEmailRequestDTO();
        request.setEmail(UserTestFixtures.VALID_EMAIL);
        User user = UserTestFixtures.sampleUser(1L);
        user.setEmailVerified(true);

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.resendVerificationEmail(request))
                .isInstanceOf(EmailAlreadyVerifiedException.class);
    }

    @Test
    void resendVerificationEmail_shouldSupportPendingEmailChanges() {
        ResendVerificationEmailRequestDTO request = new ResendVerificationEmailRequestDTO();
        request.setEmail("new-email@example.com");

        when(emailNormalizer.normalize(request.getEmail())).thenReturn("new-email@example.com");
        when(userRepository.findByEmailIgnoreCase("new-email@example.com")).thenReturn(Optional.empty());
        doNothing().when(emailVerificationService).resendVerificationCodeForPendingEmail("new-email@example.com");

        MessageResponseDTO response = authService.resendVerificationEmail(request);

        assertThat(response.getMessage()).isEqualTo("Un nouveau code a été envoyé.");
        verify(emailVerificationService).resendVerificationCodeForPendingEmail("new-email@example.com");
    }

    @Test
    void login_shouldThrowWhenEmailIsUnknown() {
        LoginRequestDTO request = AuthTestFixtures.validLoginRequest();

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Identifiants invalides.");
    }
}
