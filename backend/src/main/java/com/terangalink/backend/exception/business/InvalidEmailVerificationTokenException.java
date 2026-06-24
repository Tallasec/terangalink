package com.terangalink.backend.exception.business;

public class InvalidEmailVerificationTokenException extends RuntimeException {

    public InvalidEmailVerificationTokenException(String message) {
        super(message);
    }
}
