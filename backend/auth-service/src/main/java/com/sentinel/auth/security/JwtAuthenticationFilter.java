package com.sentinel.auth.security;

import com.sentinel.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            authenticate(header.substring(7));
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(String token) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        try {
            Claims claims = jwtTokenService.parseAndValidate(token);
            Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);
            var principal = new JwtPrincipal(claims.getSubject(), claims.get("email", String.class));
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(principal, token, authorities));
        } catch (BusinessException ignored) {
            SecurityContextHolder.clearContext();
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        Object value = claims.get("permissions");
        if (value instanceof List<?> permissions) {
            return permissions.stream()
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }
        return List.of();
    }
}

