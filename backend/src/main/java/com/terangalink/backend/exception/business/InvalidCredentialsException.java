package com.terangalink.backend.exception.business;

/**
 * Exception métier levée lors d'un échec d'authentification (login).
 * <p>
 * Rôle futur :
 * <ul>
 *   <li>Être levée par {@link com.terangalink.backend.service.AuthService} quand l'email
 *       est inconnu ou le mot de passe incorrect</li>
 *   <li>Être mappée en HTTP 401 par {@link com.terangalink.backend.exception.GlobalExceptionHandler}
 *       avec le code {@code INVALID_CREDENTIALS}</li>
 *   <li>Exposer un message unique côté API (« Identifiants invalides »)
 *       pour éviter l'énumération d'emails (bonne pratique sécurité)</li>
 * </ul>
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

}
