package com.saas.legit.core.util;

import com.saas.legit.security.CustomUserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static CustomUserDetailsService.CustomUserDetails getCurrentUser() {
        return (CustomUserDetailsService.CustomUserDetails)
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
    }
}