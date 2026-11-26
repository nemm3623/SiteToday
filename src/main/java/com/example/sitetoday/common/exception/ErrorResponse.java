package com.example.sitetoday.common.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String code, String message) {
    public static ErrorResponse from(ErrorCode error) {
        return new ErrorResponse(error.name(), error.message());
    }

    public static HttpStatus statusFor(ErrorCode error) {
        return switch (error.category()) {
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case CONFLICT -> HttpStatus.CONFLICT;
        };
    }
}
