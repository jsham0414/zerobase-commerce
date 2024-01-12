package com.zerobase.commerce.api.security;

import com.zerobase.commerce.api.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenAuthenticator tokenAuthenticator;

    private final String TOKEN_HEADER = "Authorization";

    @Value("${spring.jwt.prefix}")
    private String tokenPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException, CustomException {
        String tokenExcludedHeader = resolveTokenFromRequest(request);

        if (StringUtils.hasText(tokenExcludedHeader) && tokenAuthenticator.validateToken(tokenExcludedHeader)) {
            Authentication auth = tokenAuthenticator.getAuthentication(tokenExcludedHeader);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);
        if (!ObjectUtils.isEmpty(token) && token.startsWith(tokenPrefix)) {
            return token.substring(tokenPrefix.length());
        }

        return null;
    }
}
