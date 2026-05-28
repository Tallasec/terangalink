package com.terangalink.backend.service;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EmailNormalizer {

    public String normalize(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
