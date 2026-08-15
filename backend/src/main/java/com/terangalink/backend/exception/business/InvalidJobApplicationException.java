package com.terangalink.backend.exception.business;

public class InvalidJobApplicationException extends RuntimeException {

    public InvalidJobApplicationException(String message) {
        super(message);
    }
}
