package com.example.sitetoday.account.application;

import com.example.sitetoday.account.domain.Member;
import java.util.Objects;

public record RegisterMemberCommand(String email, String password, Member.Role role) {
    public RegisterMemberCommand {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }
        Objects.requireNonNull(role);
    }
}
