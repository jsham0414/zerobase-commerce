package com.zerobase.commerce.api.security;

import com.zerobase.commerce.database.constant.AuthorityStatus;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final String KEY_ROLES = "roles";
    private final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.prefix}")
    private String tokenPrefix;

    public String generateToken(String id, Set<AuthorityStatus> roles) {
        var claims = Jwts.claims().setSubject(id);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
