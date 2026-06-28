package com.terangalink.backend.exception.business;

public class HousingNotFoundException extends RuntimeException {

    public HousingNotFoundException(String message) {
        super(message);
    }
}
