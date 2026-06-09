package com.terangalink.backend.service;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EmailNormalizer {
    // méthodes pour gérer les espacements qu'il peut y avoir dans les emails
    public String normalize(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim(); // supprime les espacements
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
