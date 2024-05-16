package com.attica.athens.domain.common.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
        CustomException exception
    ) {
        ErrorResponse response = ErrorResponse.of(
            exception.getHttpStatus().getReasonPhrase(),
            exception.getHttpStatus().value(),
            exception.getMessage()
        );

        return ResponseEntity.status(exception.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentValidException(
        MethodArgumentNotValidException exception
    ) throws JsonProcessingException {
        BindingResult br = exception.getBindingResult();
        Map<String, String> map = new HashMap<>();
        for (FieldError fieldError : br.getFieldErrors()) {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        String message = objectMapper.writeValueAsString(map);

        ErrorResponse response = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            HttpStatus.BAD_REQUEST.value(),
            message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException exception
    ) {
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            HttpStatus.BAD_REQUEST.value(),
            exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
