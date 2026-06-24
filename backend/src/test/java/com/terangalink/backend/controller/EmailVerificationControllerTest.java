package com.terangalink.backend.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Squelette de tests pour la future implementation controller de la verification email.")
class EmailVerificationControllerTest {

    @Test
    void verifyEmail_shouldReturn204() {
        // TODO Implementer quand le comportement metier de GET /api/auth/verify-email sera active.
    }

    @Test
    void verifyEmail_shouldReturn400WhenTokenIsInvalid() {
        // TODO Implementer quand les exceptions de verification email seront branchees.
    }
}
