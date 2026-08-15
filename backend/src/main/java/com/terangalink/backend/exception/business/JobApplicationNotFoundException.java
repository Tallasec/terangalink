package com.terangalink.backend.exception.business;

public class JobApplicationNotFoundException extends RuntimeException {

    public JobApplicationNotFoundException(String message) {
        super(message);
    }
}
