package com.attica.athens.domain.common;

import com.attica.athens.domain.common.advice.ErrorResponse;

public class ApiResponse<T> {
    public boolean success;
    public T response;
    public ErrorResponse error;

    public ApiResponse(boolean success, T response, ErrorResponse error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }
}
