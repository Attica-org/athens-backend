package com.attica.athens.global.auth.filter;

import static com.attica.athens.global.auth.jwt.Constants.REQUEST_ATTRIBUTE_NAME;

import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object attribute = request.getAttribute(REQUEST_ATTRIBUTE_NAME);

        if (attribute instanceof CustomException) {
            CustomException exception = (CustomException) attribute;

            ErrorResponse error = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
            ApiResponse result = ApiUtil.failure(error);

            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(result);

            log.info("{}", exception.getMessage());
            response.setStatus(exception.getHttpStatus().value());
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(body);
        }
    }
}
