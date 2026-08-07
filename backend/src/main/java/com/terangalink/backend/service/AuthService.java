package com.terangalink.backend.service;

import com.terangalink.backend.config.JwtProperties;
import com.terangalink.backend.entity.PasswordResetToken;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.exception.business.EmailAlreadyVerifiedException;
import com.terangalink.backend.exception.business.EmailNotVerifiedException;
import com.terangalink.backend.exception.business.ExpiredPasswordResetTokenException;
import com.terangalink.backend.exception.business.InvalidCurrentPasswordException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private static final String INVALID_CREDENTIALS_MESSAGE = "Identifiants invalides.";
    private static final long PASSWORD_RESET_TOKEN_VALIDITY_MINUTES = 30;

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailNormalizer emailNormalizer;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthService(
            UserService userService,
            EmailVerificationService emailVerificationService,
            JwtService jwtService,
            JwtProperties jwtProperties,
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            EmailNormalizer emailNormalizer,
            PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailNormalizer = emailNormalizer;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Transactional
    public MessageResponseDTO register(CreateUserRequestDTO request) {
        UserResponseDTO createdUser = userService.createUser(request);
        User user = loadUserById(createdUser.getId());
        emailVerificationService.issueVerificationCode(user);
        return buildMessageResponse("Compte créé avec succès. Vérifiez votre adresse email.");
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        String normalizedEmail = emailNormalizer.normalize(request.getEmail());
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(
                    "Veuillez vérifier votre adresse email avant de vous connecter.");
        }

        return buildAuthResponse(user);
    }

    public UserResponseDTO getCurrentUser() {
        UserPrincipal principal = getCurrentPrincipal();
        return userMapper.toResponseDto(loadUserById(principal.getId()));
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDTO request) {
        UserPrincipal principal = getCurrentPrincipal();
        User user = loadUserById(principal.getId());

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException("Le mot de passe actuel est incorrect.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new SamePasswordException("Le nouveau mot de passe doit etre different du mot de passe actuel.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        String normalizedEmail = emailNormalizer.normalize(request.getEmail());

        userRepository.findByEmailIgnoreCase(normalizedEmail).ifPresent(user -> {
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(java.util.UUID.randomUUID().toString());
            passwordResetToken.setUser(user);
            passwordResetToken.setExpiresAt(
                    LocalDateTime.now().plusMinutes(PASSWORD_RESET_TOKEN_VALIDITY_MINUTES));
            passwordResetToken.setUsed(false);

            passwordResetTokenRepository.save(passwordResetToken);
            LOGGER.info(
                    "Token de reinitialisation de mot de passe genere pour l'utilisateur {} : {}",
                    user.getId(),
                    passwordResetToken.getToken()
            );
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidPasswordResetTokenException(
                        "Le token de reinitialisation est invalide."));

        if (passwordResetToken.isUsed()) {
            throw new InvalidPasswordResetTokenException("Le token de reinitialisation a deja ete utilise.");
        }

        if (!passwordResetToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ExpiredPasswordResetTokenException("Le token de reinitialisation a expire.");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        passwordResetToken.setUsed(true);

        userRepository.save(user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequestDTO request) {
        emailVerificationService.verifyEmail(request);
    }

    @Transactional
    public MessageResponseDTO resendVerificationEmail(ResendVerificationEmailRequestDTO request) {
        String normalizedEmail = emailNormalizer.normalize(request.getEmail());

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
        if (user != null) {
            if (user.isEmailVerified()) {
                throw new EmailAlreadyVerifiedException("L'adresse email de cet utilisateur a deja ete verifiee.");
            }

            emailVerificationService.resendVerificationCode(user);
            return buildMessageResponse("Un nouveau code a été envoyé.");
        }

        emailVerificationService.resendVerificationCodeForPendingEmail(normalizedEmail);
        return buildMessageResponse("Un nouveau code a été envoyé.");
    }

    private UserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new InvalidCredentialsException("Utilisateur non authentifie.");
        }

        return principal;
    }

    private User loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'id : " + id));
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        UserPrincipal principal = UserPrincipal.from(user);
        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(jwtService.generateToken(principal));
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtProperties.getExpirationMs() / 1000);
        response.setUser(userMapper.toResponseDto(user));
        return response;
    }

    private MessageResponseDTO buildMessageResponse(String message) {
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage(message);
        return response;
    }
}
