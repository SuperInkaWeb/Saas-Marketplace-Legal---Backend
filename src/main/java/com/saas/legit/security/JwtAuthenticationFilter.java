package com.saas.legit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        jwtService.validateAndExtractClaims(jwt).ifPresent(claims -> {

            String userIdStr = claims.getSubject();
            String email = claims.get("email", String.class);
            Long tenantId = claims.get("tenant", Long.class);
            List<?> rawRoles = claims.get("roles", List.class);

            if (userIdStr != null && rawRoles != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Long userId = Long.parseLong(userIdStr);

                var authorities = rawRoles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.toString()))
                        .toList();

                CustomUserDetailsService.CustomUserDetails userDetails =
                        new CustomUserDetailsService.CustomUserDetails(
                                userId,
                                email,
                                null,
                                authorities,
                                tenantId
                        );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        });

        filterChain.doFilter(request, response);
    }
}