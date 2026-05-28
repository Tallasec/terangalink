package com.terangalink.backend.exception.business;

public class InvalidUserPatchException extends RuntimeException {
    public InvalidUserPatchException(String message) {
        super(message);
    }
}
