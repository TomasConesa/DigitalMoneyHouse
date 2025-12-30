package com.digitalmoneyhouse.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            return expiration == null || expiration.after(new Date());
        } catch (io.jsonwebtoken.JwtException e) {
            return false;
        }
    }

    public String getEmail(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (io.jsonwebtoken.JwtException e) {
            return null;
        }
    }

    public String getUserId(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("id", String.class);
        } catch (io.jsonwebtoken.JwtException e) {
            return null;
        }
    }

   // @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        try {
            Claims claims = getClaims(token);
            Object roles = claims.get("roles");
            if (roles instanceof List<?>) {
                return ((List<?>) roles).stream()
                        .map(Object::toString)
                        .toList();
            }
            return List.of();
        } catch (io.jsonwebtoken.JwtException e) {
            return List.of();
        }
    }
}
