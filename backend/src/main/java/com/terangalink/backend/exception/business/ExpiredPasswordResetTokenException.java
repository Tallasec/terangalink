package com.terangalink.backend.exception.business;

public class ExpiredPasswordResetTokenException extends RuntimeException {

    public ExpiredPasswordResetTokenException(String message) {
        super(message);
    }
}
