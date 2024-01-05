package com.zerobase.commerce.api.security;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.user.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenAuthenticator {
    private final AuthService authService;
    private final String TOKEN_HEADER = "Authorization";

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.prefix}")
    private String tokenPrefix;

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    private String getId(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token))
            return false;

        return !parseClaims(token).getExpiration().before(new Date());
    }

    Authentication getAuthentication(String jwt) {
        var userDetails = authService.loadUserByUsername(getId(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveTokenFromHeader(HttpHeaders headers) {
        String token = headers.getFirst(TOKEN_HEADER);

        if (token == null || token.isEmpty() || !token.startsWith(tokenPrefix))
            throw new CustomException(ErrorCode.INVALID_TOKEN);

        return getId(token.substring(tokenPrefix.length()));
    }
}
