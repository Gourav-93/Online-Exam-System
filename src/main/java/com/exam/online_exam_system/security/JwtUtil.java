package com.exam.online_exam_system.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final String SECRET_KEY = "9a67471ec635249314b77c6d5671f5367d3e4492ef43c49e7b25e2e88a38a3d4";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String email, String role) {
        log.debug("Generating JWT token for subject/email: {}", email);
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        log.debug("Successfully generated JWT token for subject/email: {}", email);
        return token;
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        boolean isExpired = isTokenExpired(token);
        boolean usernameMatches = username.equalsIgnoreCase(userDetails.getUsername());
        
        log.debug("Validating token: extracted username='{}', userDetails username='{}', isExpired={}", 
                username, userDetails.getUsername(), isExpired);

        if (!usernameMatches) {
            log.warn("Token validation failed: extracted username '{}' does not match userDetails username '{}'", 
                    username, userDetails.getUsername());
        }
        if (isExpired) {
            log.warn("Token validation failed: token is expired");
        }

        return (usernameMatches && !isExpired);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        boolean expired = expiration.before(new Date());
        if (expired) {
            log.debug("Token expiration check: expired at {} (current time is {})", expiration, new Date());
        }
        return expired;
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse JWT claims: {}", e.getMessage());
            throw e;
        }
    }
}