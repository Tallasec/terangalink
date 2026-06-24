package com.terangalink.backend.service;

import com.terangalink.backend.config.JwtProperties;
import com.terangalink.backend.entity.EmailVerificationToken;
import com.terangalink.backend.entity.PasswordResetToken;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.exception.business.EmailAlreadyVerifiedException;
import com.terangalink.backend.exception.business.EmailAlreadyExistsException;
import com.terangalink.backend.exception.business.EmailNotVerifiedException;
import com.terangalink.backend.exception.business.ExpiredEmailVerificationTokenException;
import com.terangalink.backend.exception.business.ExpiredPasswordResetTokenException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.InvalidCurrentPasswordException;
import com.terangalink.backend.exception.business.InvalidEmailVerificationTokenException;
import com.terangalink.backend.exception.business.InvalidPasswordResetTokenException;
import com.terangalink.backend.exception.business.SamePasswordException;
import com.terangalink.backend.mapper.UserMapper;
import com.terangalink.backend.repository.EmailVerificationTokenRepository;
import com.terangalink.backend.repository.PasswordResetTokenRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.ChangePasswordRequestDTO;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.ForgotPasswordRequestDTO;
import com.terangalink.backend.requestDTO.LoginRequestDTO;
import com.terangalink.backend.requestDTO.ResetPasswordRequestDTO;
import com.terangalink.backend.requestDTO.VerifyEmailRequestDTO;
import com.terangalink.backend.responseDTO.AuthResponseDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.support.AuthTestFixtures;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailNormalizer emailNormalizer;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_shouldDelegateToUserServiceAndReturnAuthResponse() {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();
        UserResponseDTO createdUser = UserTestFixtures.sampleUserResponse(1L);
        User savedUser = UserTestFixtures.sampleUser(1L);

        when(userService.createUser(request)).thenReturn(createdUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(jwtProperties.getExpirationMs()).thenReturn(86_400_000L);
        when(jwtService.generateToken(any(UserPrincipal.class))).thenReturn("jwt-token");
        when(userMapper.toResponseDto(savedUser)).thenReturn(createdUser);

        AuthResponseDTO response = authService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86_400L);
        assertThat(response.getUser()).isEqualTo(createdUser);
        verify(userService).createUser(request);
        ArgumentCaptor<EmailVerificationToken> tokenCaptor = ArgumentCaptor.forClass(EmailVerificationToken.class);
        verify(emailVerificationTokenRepository).save(tokenCaptor.capture());
        EmailVerificationToken savedToken = tokenCaptor.getValue();
        assertThatCode(() -> UUID.fromString(savedToken.getToken())).doesNotThrowAnyException();
        assertThat(savedToken.getUser()).isEqualTo(savedUser);
        assertThat(savedToken.isUsed()).isFalse();
        assertThat(savedToken.getExpiresAt()).isAfter(LocalDateTime.now().minusMinutes(1));
        assertThat(savedToken.getExpiresAt()).isBefore(LocalDateTime.now().plusHours(25));
    }

    @Test
    void register_shouldPropagateEmailAlreadyExistsException() {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();
        when(userService.createUser(request))
                .thenThrow(new EmailAlreadyExistsException("Un utilisateur existe déjà avec cet email."));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(jwtService, never()).generateToken(any());
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
                .isInstanceOf(EmailNotVerifiedException.class)
                .hasMessage("Veuillez vérifier votre adresse email avant de vous connecter.");
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

    @Test
    void login_shouldThrowWhenPasswordIsIncorrect() {
        LoginRequestDTO request = AuthTestFixtures.validLoginRequest();
        User user = UserTestFixtures.sampleUser(1L);

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(UserTestFixtures.VALID_PASSWORD, user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Identifiants invalides.");
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
    void getCurrentUser_shouldThrowWhenNotAuthenticated() {
        assertThatThrownBy(() -> authService.getCurrentUser())
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Utilisateur non authentifie.");
    }

    @Test
    void changePassword_shouldEncodeAndSaveNewPassword() {
        User user = UserTestFixtures.sampleUser(1L);
        UserPrincipal principal = UserPrincipal.from(user);
        ChangePasswordRequestDTO request = changePasswordRequest("Password1!", "NewPassword2!");

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
    void changePassword_shouldThrowWhenCurrentPasswordIsIncorrect() {
        User user = UserTestFixtures.sampleUser(1L);
        UserPrincipal principal = UserPrincipal.from(user);
        ChangePasswordRequestDTO request = changePasswordRequest("WrongPassword1!", "NewPassword2!");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(request))
                .isInstanceOf(InvalidCurrentPasswordException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void changePassword_shouldThrowWhenNewPasswordIsSameAsCurrentPassword() {
        User user = UserTestFixtures.sampleUser(1L);
        UserPrincipal principal = UserPrincipal.from(user);
        ChangePasswordRequestDTO request = changePasswordRequest("Password1!", "Password1!");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> authService.changePassword(request))
                .isInstanceOf(SamePasswordException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void forgotPassword_shouldCreateTokenWhenUserExists() {
        ForgotPasswordRequestDTO request = forgotPasswordRequest(UserTestFixtures.VALID_EMAIL);
        User user = UserTestFixtures.sampleUser(1L);
        LocalDateTime beforeCreation = LocalDateTime.now().plusMinutes(29);

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL))
                .thenReturn(Optional.of(user));

        authService.forgotPassword(request);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();

        assertThatCode(() -> UUID.fromString(savedToken.getToken())).doesNotThrowAnyException();
        assertThat(savedToken.getUser()).isEqualTo(user);
        assertThat(savedToken.isUsed()).isFalse();
        assertThat(savedToken.getExpiresAt())
                .isAfter(beforeCreation)
                .isBefore(LocalDateTime.now().plusMinutes(31));
    }

    @Test
    void forgotPassword_shouldDoNothingWhenUserDoesNotExist() {
        ForgotPasswordRequestDTO request = forgotPasswordRequest("unknown@example.com");

        when(emailNormalizer.normalize(request.getEmail())).thenReturn("unknown@example.com");
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        authService.forgotPassword(request);

        verifyNoInteractions(passwordResetTokenRepository);
    }

    @Test
    void resetPassword_shouldUpdatePasswordAndMarkTokenAsUsed() {
        User user = UserTestFixtures.sampleUser(1L);
        PasswordResetToken token = passwordResetToken(user, LocalDateTime.now().plusMinutes(10), false);
        ResetPasswordRequestDTO request = resetPasswordRequest(token.getToken(), "NewPassword2!");

        when(passwordResetTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("new-encoded-password");

        authService.resetPassword(request);

        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
        assertThat(token.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).save(token);
    }

    @Test
    void resetPassword_shouldThrowWhenTokenDoesNotExist() {
        ResetPasswordRequestDTO request = resetPasswordRequest("missing-token", "NewPassword2!");
        when(passwordResetTokenRepository.findByToken(request.getToken())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(InvalidPasswordResetTokenException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_shouldThrowWhenTokenIsExpired() {
        User user = UserTestFixtures.sampleUser(1L);
        PasswordResetToken token = passwordResetToken(user, LocalDateTime.now().minusMinutes(1), false);
        ResetPasswordRequestDTO request = resetPasswordRequest(token.getToken(), "NewPassword2!");
        when(passwordResetTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(ExpiredPasswordResetTokenException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_shouldThrowWhenTokenWasAlreadyUsed() {
        User user = UserTestFixtures.sampleUser(1L);
        PasswordResetToken token = passwordResetToken(user, LocalDateTime.now().plusMinutes(10), true);
        ResetPasswordRequestDTO request = resetPasswordRequest(token.getToken(), "NewPassword2!");
        when(passwordResetTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(InvalidPasswordResetTokenException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyEmail_shouldActivateUserAndMarkTokenAsUsed() {
        User user = UserTestFixtures.sampleUser(1L);
        user.setEmailVerified(false);
        EmailVerificationToken token = emailVerificationToken(user, LocalDateTime.now().plusHours(1), false);
        VerifyEmailRequestDTO request = verifyEmailRequest(token.getToken());

        when(emailVerificationTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        authService.verifyEmail(request);

        assertThat(user.isEmailVerified()).isTrue();
        assertThat(token.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(emailVerificationTokenRepository).save(token);
    }

    @Test
    void verifyEmail_shouldRejectUnknownToken() {
        VerifyEmailRequestDTO request = verifyEmailRequest("missing-token");
        when(emailVerificationTokenRepository.findByToken(request.getToken())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyEmail(request))
                .isInstanceOf(InvalidEmailVerificationTokenException.class)
                .hasMessage("Le token de verification email est invalide.");

        verify(userRepository, never()).save(any(User.class));
        verify(emailVerificationTokenRepository, never()).save(any(EmailVerificationToken.class));
    }

    @Test
    void verifyEmail_shouldRejectExpiredToken() {
        User user = UserTestFixtures.sampleUser(1L);
        EmailVerificationToken token = emailVerificationToken(user, LocalDateTime.now().minusMinutes(1), false);
        VerifyEmailRequestDTO request = verifyEmailRequest(token.getToken());

        when(emailVerificationTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyEmail(request))
                .isInstanceOf(ExpiredEmailVerificationTokenException.class)
                .hasMessage("Le token de verification email a expire.");

        verify(userRepository, never()).save(any(User.class));
        verify(emailVerificationTokenRepository, never()).save(any(EmailVerificationToken.class));
    }

    @Test
    void verifyEmail_shouldRejectAlreadyUsedToken() {
        User user = UserTestFixtures.sampleUser(1L);
        EmailVerificationToken token = emailVerificationToken(user, LocalDateTime.now().plusHours(1), true);
        VerifyEmailRequestDTO request = verifyEmailRequest(token.getToken());

        when(emailVerificationTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyEmail(request))
                .isInstanceOf(InvalidEmailVerificationTokenException.class)
                .hasMessage("Le token de verification email a deja ete utilise.");

        verify(userRepository, never()).save(any(User.class));
        verify(emailVerificationTokenRepository, never()).save(any(EmailVerificationToken.class));
    }

    @Test
    void verifyEmail_shouldRejectAlreadyVerifiedUser() {
        User user = UserTestFixtures.sampleUser(1L);
        user.setEmailVerified(true);
        EmailVerificationToken token = emailVerificationToken(user, LocalDateTime.now().plusHours(1), false);
        VerifyEmailRequestDTO request = verifyEmailRequest(token.getToken());

        when(emailVerificationTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyEmail(request))
                .isInstanceOf(EmailAlreadyVerifiedException.class)
                .hasMessage("L'adresse email de cet utilisateur a deja ete verifiee.");

        verify(userRepository, never()).save(any(User.class));
        verify(emailVerificationTokenRepository, never()).save(any(EmailVerificationToken.class));
    }

    private ChangePasswordRequestDTO changePasswordRequest(String currentPassword, String newPassword) {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword(currentPassword);
        request.setNewPassword(newPassword);
        return request;
    }

    private ForgotPasswordRequestDTO forgotPasswordRequest(String email) {
        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO();
        request.setEmail(email);
        return request;
    }

    private ResetPasswordRequestDTO resetPasswordRequest(String token, String newPassword) {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setToken(token);
        request.setNewPassword(newPassword);
        return request;
    }

    private VerifyEmailRequestDTO verifyEmailRequest(String token) {
        VerifyEmailRequestDTO request = new VerifyEmailRequestDTO();
        request.setToken(token);
        return request;
    }

    private PasswordResetToken passwordResetToken(User user, LocalDateTime expiresAt, boolean used) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(expiresAt);
        token.setUsed(used);
        return token;
    }

    private EmailVerificationToken emailVerificationToken(User user, LocalDateTime expiresAt, boolean used) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(expiresAt);
        token.setUsed(used);
        return token;
    }
}
