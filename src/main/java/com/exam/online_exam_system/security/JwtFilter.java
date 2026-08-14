package com.exam.online_exam_system.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

        private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

        @Autowired
        private JwtUtil jwtUtil;


        private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
                String path = request.getRequestURI();
                boolean shouldNot = path.startsWith("/auth/") || path.equals("/error");
                log.debug("Checking path '{}' in shouldNotFilter: {}", path, shouldNot);
                return shouldNot;
        }

        private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
                log.warn("Authentication failed: writing 401 response with message '{}'", message);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(String.format(
                        "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}",
                        LocalDateTime.now(), message
                ));
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");
                log.debug("JwtFilter processing request: {} {}, Authorization Header: {}", 
                        request.getMethod(), request.getRequestURI(), 
                        (authHeader != null ? "Present (Length: " + authHeader.length() + ")" : "NULL"));

                String token = null;
                String username = null;
                String role = null;

                // Extract token safely and support case-insensitive "Bearer " or "bearer "
                if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
                        token = authHeader.substring(7).trim();
                        log.debug("Extracted JWT Token: [length={}]", token.length());

                        try {
                                username = jwtUtil.extractUsername(token);
                                role = jwtUtil.extractRole(token);
                                log.debug("Extracted username: '{}', role: '{}'", username, role);
                        } catch (io.jsonwebtoken.ExpiredJwtException e) {
                                log.error("JWT validation error: Token has expired. {}", e.getMessage());
                                writeErrorResponse(response, "JWT token has expired");
                                return;
                        } catch (io.jsonwebtoken.security.SignatureException e) {
                                log.error("JWT validation error: Invalid signature. {}", e.getMessage());
                                writeErrorResponse(response, "Invalid JWT token signature");
                                return;
                        } catch (io.jsonwebtoken.MalformedJwtException e) {
                                log.error("JWT validation error: Malformed token. {}", e.getMessage());
                                writeErrorResponse(response, "Malformed JWT token format");
                                return;
                        } catch (Exception e) {
                                log.error("JWT validation error: General parsing error. {}", e.getMessage());
                                writeErrorResponse(response, "Invalid or malformed JWT token");
                                return;
                        }
                } else if (authHeader != null) {
                        log.warn("Authorization header found but does not start with 'Bearer ' or 'bearer '");
                }

                // Authenticate only if security context is empty
                if (username != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        log.debug("Security Context is empty, setting stateless authentication for username: '{}'", username);
                        try {
                                if (!jwtUtil.isTokenExpired(token)) {
                                        String finalRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                                        username,
                                                        null,
                                                        Collections.singleton(new SimpleGrantedAuthority(finalRole)));

                                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                                        context.setAuthentication(authToken);
                                        SecurityContextHolder.setContext(context);
                                        
                                        securityContextRepository.saveContext(context, request, response);
                                        
                                        log.debug("Successfully authenticated user '{}' (stateless)", username);
                                } else {
                                        log.warn("JWT Token is expired for user '{}'", username);
                                }
                        } catch (Exception e) {
                                log.error("Error setting authentication in Security Context: {}", e.getMessage());
                        }
                } else if (username != null) {
                        log.debug("Security Context already has authentication: {}", 
                                SecurityContextHolder.getContext().getAuthentication());
                }

                filterChain.doFilter(request, response);
        }
}