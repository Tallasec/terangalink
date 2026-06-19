package com.terangalink.backend.exception;

import com.terangalink.backend.exception.business.EmailAlreadyExistsException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du {@link GlobalExceptionHandler} : mapping exception → réponse
 * HTTP standardisée ({@link ApiErrorResponse}).
 * <p>
 * Utile pour verrouiller le contrat d'erreur de l'API (codes, libellés, structure
 * JSON) indépendamment des contrôleurs.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String REQUEST_PATH = "/api/users";

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(REQUEST_PATH);
    }

    @Test
    void handleValidationErrors_shouldReturn400WithFieldDetails() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createUserRequestDTO");
        bindingResult.addError(new FieldError("createUserRequestDTO", "email", "Le format de l'email est invalide."));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationErrors(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("VALIDATION_ERROR");
        assertThat(body.getMessage()).isEqualTo("Les données de la requête sont invalides.");
        assertThat(body.getPath()).isEqualTo(REQUEST_PATH);
        assertThat(body.getDetails()).containsEntry("email", "Le format de l'email est invalide.");
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void handleConstraintViolation_shouldReturn400WithViolationDetails() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("getUserByEmail.email");
        when(violation.getMessage()).thenReturn("Le format de l'email est invalide.");
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ApiErrorResponse> response = handler.handleConstraintViolation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getError()).isEqualTo("CONSTRAINT_VIOLATION");
        assertThat(body.getDetails()).containsEntry("getUserByEmail.email", "Le format de l'email est invalide.");
    }

    @Test
    void handleEmailAlreadyExists_shouldReturn409() {
        EmailAlreadyExistsException exception =
                new EmailAlreadyExistsException("Un utilisateur existe déjà avec cet email.");

        ResponseEntity<ApiErrorResponse> response = handler.handleEmailAlreadyExists(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(409);
        assertThat(body.getError()).isEqualTo("EMAIL_ALREADY_EXISTS");
        assertThat(body.getMessage()).isEqualTo("Un utilisateur existe déjà avec cet email.");
        assertThat(body.getPath()).isEqualTo(REQUEST_PATH);
        assertThat(body.getDetails()).isNull();
    }

    @Test
    void handleUserNotFound_shouldReturn404() {
        UserNotFoundException exception =
                new UserNotFoundException("Utilisateur introuvable avec l'id : 42");

        ResponseEntity<ApiErrorResponse> response = handler.handleUserNotFound(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(404);
        assertThat(body.getError()).isEqualTo("USER_NOT_FOUND");
        assertThat(body.getMessage()).isEqualTo("Utilisateur introuvable avec l'id : 42");
    }

    @Test
    void handleInvalidCredentials_shouldReturn401() {
        InvalidCredentialsException exception =
                new InvalidCredentialsException("Identifiants invalides.");

        ResponseEntity<ApiErrorResponse> response = handler.handleInvalidCredentials(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(401);
        assertThat(body.getError()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(body.getMessage()).isEqualTo("Identifiants invalides.");
    }

    @Test
    void handleIllegalArgument_shouldReturn400WithMessage() {
        IllegalArgumentException exception =
                new IllegalArgumentException("Le champ de tri 'foo' n'est pas autorise.");

        ResponseEntity<ApiErrorResponse> response = handler.handleIllegalArgument(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getError()).isEqualTo("INVALID_REQUEST_PARAMETER");
        assertThat(body.getMessage()).isEqualTo("Le champ de tri 'foo' n'est pas autorise.");
    }

    @Test
    void handleGenericException_shouldReturn500WithoutExposingInternalDetails() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleGenericException(new RuntimeException("secret internal detail"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(500);
        assertThat(body.getError()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(body.getMessage()).isEqualTo("Une erreur interne est survenue.");
        assertThat(body.getMessage()).doesNotContain("secret internal detail");
    }
}
