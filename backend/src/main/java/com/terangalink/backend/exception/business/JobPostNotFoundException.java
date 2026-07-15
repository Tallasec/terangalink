package com.terangalink.backend.exception.business;

public class JobPostNotFoundException extends RuntimeException {

    public JobPostNotFoundException(String message) {
        super(message);
    }
}
