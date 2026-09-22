package com.tuckersoft.branchengine.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuckersoft.branchengine.web.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ErrorResponse errorResponse = new ErrorResponse(
                "FORBIDDEN",
                accessDeniedException.getMessage() != null ? accessDeniedException.getMessage() : "Forbidden",
                Instant.now().toString(),
                request.getRequestURI()
        );
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
