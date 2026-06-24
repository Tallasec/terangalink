package com.terangalink.backend.exception.business;

public class ExpiredEmailVerificationTokenException extends RuntimeException {

    public ExpiredEmailVerificationTokenException(String message) {
        super(message);
    }
}
