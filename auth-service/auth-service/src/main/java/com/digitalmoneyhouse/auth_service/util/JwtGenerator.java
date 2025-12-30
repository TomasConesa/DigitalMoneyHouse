package com.digitalmoneyhouse.auth_service.util;

import com.digitalmoneyhouse.auth_service.exceptions.CustomJwtException;
import com.digitalmoneyhouse.auth_service.exceptions.JwtExpiredException;
import com.digitalmoneyhouse.auth_service.exceptions.JwtInvalidException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;


@Component
public class JwtGenerator {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String email, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .claim("id", userId.toString())
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("Token expirado");
        } catch (JwtException e) {
            throw new JwtInvalidException("Token inválido");
        }
    }

    public List<String> extractRoles(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("roles", List.class);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("Token expirado");
        } catch (JwtException e) {
            throw new JwtInvalidException("Token inválido");
        }
    }

    public boolean validateToken(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (CustomJwtException e) {
            return false;
        }
    }

}
