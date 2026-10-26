package com.example.cashier.infrastructures.middleware;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.cashier.infrastructures.security.JwtTokenManager;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;

    public JwtAuthFilter(JwtTokenManager jwtTokenManager) {
        this.jwtTokenManager = jwtTokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        log.info("Path : {}", path);
        if (path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/refresh")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.info("Token (Masked) : {}", com.example.cashier.commons.utils.MaskingUtil.maskToken(token));
                String username = jwtTokenManager.extractUsername(token);
                String role = jwtTokenManager.extractRole(token);
                log.info("Username : {}", username);
                log.info("Role : {}", role);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    UsernamePasswordAuthenticationToken ktpUser = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singleton(authority));
                    SecurityContextHolder.getContext().setAuthentication(ktpUser);
                }
            }
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token tidak valid atau sudah kadaluarsa!\"}");
            return;
        }

        filterChain.doFilter(request, response);

    }

}
