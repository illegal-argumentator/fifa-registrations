package com.daniel_niepmann.registrations.common.exception.handler;

import com.daniel_niepmann.registrations.common.exception.EntityAlreadyExistsException;
import com.daniel_niepmann.registrations.common.exception.ExceptionResponse;
import com.daniel_niepmann.registrations.common.exception.FileReadException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(FileReadException.class)
    public ResponseEntity<ExceptionResponse> handleFileReadException(FileReadException exception, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ExceptionResponse.builder()
                        .message(exception.getMessage())
                        .code(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                .build());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ExceptionResponse.builder()
                .message(exception.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .path(request.getRequestURI())
                .build());
    }

}
