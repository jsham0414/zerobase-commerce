package com.zerobase.commerce.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            writeErrorResponse(response, e);
        } catch (RuntimeException e) {
            e.printStackTrace();
            writeErrorResponse(response, new CustomException(ErrorCode.UNEXPECTED_ERROR));
        }
    }

    private void writeErrorResponse(HttpServletResponse response, CustomException e) throws IOException {
        String errorJson = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(new ErrorResponse(e).toResponseEntity().getBody());

        response.setStatus(e.getErrorCode().getStatusCode().value());
        response.setContentType("application/json");
        response.getWriter().write(errorJson);
    }
}
