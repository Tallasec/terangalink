package com.terangalink.backend.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Squelette de tests pour la future implementation metier de la verification email.")
class EmailVerificationServiceTest {

    @Test
    void verifyEmail_shouldActivateUserAndMarkTokenAsUsed() {
        // TODO Implementer quand AuthService.verifyEmail contiendra la logique metier.
    }

    @Test
    void verifyEmail_shouldRejectUnknownToken() {
        // TODO Implementer quand la verification des tokens email sera active.
    }

    @Test
    void verifyEmail_shouldRejectExpiredToken() {
        // TODO Implementer quand la verification des tokens email sera active.
    }
}
