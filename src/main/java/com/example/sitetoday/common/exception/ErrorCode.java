package com.example.sitetoday.common.exception;

public interface ErrorCode {
    enum Category { INVALID_INPUT, UNAUTHENTICATED, FORBIDDEN, CONFLICT }

    String name();
    String message();
    Category category();
}
