package com.attica.athens.domain.common.advice;

import com.attica.athens.domain.common.ApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
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
    public ResponseEntity<?> handleCustomException(
            CustomException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                exception.getErrorCode(),
                exception.getMessage()
        );

        return ResponseEntity.status(exception.getHttpStatus())
                .body(ApiUtil.failure(response));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentValidException(
            MethodArgumentNotValidException exception
    ) throws JsonProcessingException {
        BindingResult br = exception.getBindingResult();
        Map<String, String> map = new HashMap<>();
        for (FieldError fieldError : br.getFieldErrors()) {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        String message = objectMapper.writeValueAsString(map);

        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_FAILED.getCode(),
                message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiUtil.failure(response));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                ErrorCode.WRONG_REQUEST_TRANSMISSION.getCode(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiUtil.failure(response));
    }

    @ExceptionHandler(TimeoutException.class)
    ResponseEntity<?> handleTimeLimiterException() {

        ErrorResponse response = new ErrorResponse(
                ErrorCode.WRONG_REQUEST_TRANSMISSION.getCode(),
                "요청이 타임아웃되었습니다."
        );
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ApiUtil.failure(response));

    }
}
