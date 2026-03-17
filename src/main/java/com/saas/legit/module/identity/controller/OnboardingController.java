package com.saas.legit.module.identity.controller;

import com.saas.legit.module.identity.dto.CreateClientProfileRequest;
import com.saas.legit.module.identity.dto.CreateLawyerProfileRequest;
import com.saas.legit.module.identity.dto.SelectRoleRequest;
import com.saas.legit.module.identity.dto.UserMeResponse;
import com.saas.legit.module.identity.service.OnboardingService;
import com.saas.legit.module.identity.service.UserService;
import com.saas.legit.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final UserService userService;

    @PostMapping("/select-role")
    public ResponseEntity<UserMeResponse> selectRole(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal,
            @Valid @RequestBody SelectRoleRequest request
    ) {
        onboardingService.selectRole(principal.userId(), request);
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }

    @PostMapping("/profile/client")
    public ResponseEntity<UserMeResponse> createClientProfile(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal,
            @Valid @RequestBody CreateClientProfileRequest request
    ) {
        onboardingService.createClientProfile(principal.userId(), request);
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }

    @PostMapping("/profile/lawyer")
    public ResponseEntity<UserMeResponse> createLawyerProfile(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal,
            @Valid @RequestBody CreateLawyerProfileRequest request
    ) {
        onboardingService.createLawyerProfile(principal.userId(), request);
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }

    @GetMapping("/status")
    public ResponseEntity<UserMeResponse> getOnboardingStatus(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal
    ) {
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }
}
