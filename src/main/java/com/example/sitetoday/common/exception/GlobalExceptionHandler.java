package com.example.sitetoday.common.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        ErrorCode error = exception.errorCode();
        return ResponseEntity.status(ErrorResponse.statusFor(error)).body(ErrorResponse.from(error));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse error = status.value() == 400
                ? ErrorResponse.from(CommonErrorCode.INVALID_REQUEST)
                : new ErrorResponse("HTTP_" + status.value(), "요청을 처리할 수 없습니다.");
        return super.handleExceptionInternal(exception, error, headers, status, request);
    }
}
