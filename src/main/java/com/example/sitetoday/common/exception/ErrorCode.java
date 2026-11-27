package com.example.sitetoday.common.exception;

public interface ErrorCode {
    enum Category { INVALID_INPUT, UNAUTHENTICATED, FORBIDDEN, NOT_FOUND, CONFLICT }

    String name();
    String message();
    Category category();
}
