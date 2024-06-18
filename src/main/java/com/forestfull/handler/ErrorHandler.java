package com.forestfull.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<Map<String, String>> isRuntimeException(Exception e) {
        return ResponseEntity.badRequest()
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> isInternalServerError(Exception e) {
        return ResponseEntity.internalServerError()
                .body(Collections.singletonMap("message", e.getMessage()));
    }

}