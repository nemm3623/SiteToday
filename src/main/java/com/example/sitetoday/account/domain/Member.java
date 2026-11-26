package com.example.sitetoday.account.domain;

import java.util.Locale;
import java.util.UUID;

public record Member(UUID id, String email, String passwordHash, Role role) {
    public enum Role { WORKER, EMPLOYER, OFFICE }

    public static String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
