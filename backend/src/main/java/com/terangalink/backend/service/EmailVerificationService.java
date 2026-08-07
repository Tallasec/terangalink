package com.terangalink.backend.service;

import com.terangalink.backend.entity.EmailVerificationToken;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.exception.business.EmailAlreadyVerifiedException;
import com.terangalink.backend.exception.business.ExpiredEmailVerificationTokenException;
import com.terangalink.backend.exception.business.InvalidEmailVerificationTokenException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.repository.EmailVerificationTokenRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.VerifyEmailRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class EmailVerificationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long EMAIL_VERIFICATION_TOKEN_VALIDITY_MINUTES = 10;

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EmailVerificationService(
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void issueVerificationCode(User user) {
        issueVerificationCode(user, user.getEmail(), null);
    }

    @Transactional
    public void issueEmailChangeVerificationCode(User user, String pendingEmail) {
        issueVerificationCode(user, pendingEmail, pendingEmail);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequestDTO request) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new InvalidEmailVerificationTokenException(
                        "Le token de verification email est invalide."));

        if (emailVerificationToken.isUsed()) {
            throw new InvalidEmailVerificationTokenException(
                    "Le token de verification email a deja ete utilise.");
        }

        if (!emailVerificationToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ExpiredEmailVerificationTokenException(
                    "Le token de verification email a expire.");
        }

        User user = emailVerificationToken.getUser();
        if (user.isEmailVerified() && emailVerificationToken.getPendingEmail() == null) {
            throw new EmailAlreadyVerifiedException(
                    "L'adresse email de cet utilisateur a deja ete verifiee.");
        }

        if (emailVerificationToken.getPendingEmail() != null) {
            user.setEmail(emailVerificationToken.getPendingEmail());
        }

        user.setEmailVerified(true);
        emailVerificationToken.setUsed(true);

        userRepository.save(user);
        emailVerificationTokenRepository.save(emailVerificationToken);
    }

    @Transactional
    public void resendVerificationCode(User user) {
        issueVerificationCode(user, user.getEmail(), null);
    }

    @Transactional
    public void resendVerificationCodeForPendingEmail(String pendingEmail) {
        EmailVerificationToken token = emailVerificationTokenRepository
                .findFirstByPendingEmailAndUsedFalseOrderByExpiresAtDesc(pendingEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "Aucune demande de verification n'existe pour cette adresse email."));

        issueVerificationCode(token.getUser(), pendingEmail, pendingEmail);
    }

    private void issueVerificationCode(User user, String targetEmail, String pendingEmail) {
        invalidateUnusedTokens(user);

        String token = generateUniqueEmailVerificationCode();

        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setPendingEmail(pendingEmail);
        emailVerificationToken.setExpiresAt(
                LocalDateTime.now().plusMinutes(EMAIL_VERIFICATION_TOKEN_VALIDITY_MINUTES));
        emailVerificationToken.setUsed(false);

        emailVerificationTokenRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(
                Objects.requireNonNull(targetEmail),
                user.getFirstName(),
                token
        );
    }

    private void invalidateUnusedTokens(User user) {
        List<EmailVerificationToken> unusedTokens =
                emailVerificationTokenRepository.findAllByUserAndUsedFalse(user);

        for (EmailVerificationToken token : unusedTokens) {
            token.setUsed(true);
        }

        if (!unusedTokens.isEmpty()) {
            emailVerificationTokenRepository.saveAll(unusedTokens);
        }
    }

    private String generateUniqueEmailVerificationCode() {
        String token;

        do {
            token = generateEmailVerificationCode();
        } while (emailVerificationTokenRepository.existsByToken(token));

        return token;
    }

    private String generateEmailVerificationCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1000000));
    }
}
