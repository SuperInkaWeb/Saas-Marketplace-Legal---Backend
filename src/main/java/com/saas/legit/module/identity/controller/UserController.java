package com.saas.legit.module.identity.controller;

import com.saas.legit.module.identity.dto.UserMeResponse;
import com.saas.legit.module.identity.service.UserService;
import com.saas.legit.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMe(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal
    ) {
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }
}
