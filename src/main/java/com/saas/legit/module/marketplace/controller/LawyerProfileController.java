package com.saas.legit.module.marketplace.controller;

import com.saas.legit.module.identity.dto.UserMeResponse;
import com.saas.legit.module.identity.service.UserService;
import com.saas.legit.module.marketplace.dto.CreateLawyerProfileRequest;
import com.saas.legit.module.marketplace.service.LawyerProfileConfigService;
import com.saas.legit.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles/lawyer")
@RequiredArgsConstructor
public class LawyerProfileController {

    private final LawyerProfileConfigService lawyerProfileService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserMeResponse> createLawyerProfile(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal,
            @Valid @RequestBody CreateLawyerProfileRequest request
    ) {
        lawyerProfileService.createLawyerProfile(principal.userId(), request);
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }

    @GetMapping("/status")
    public ResponseEntity<UserMeResponse> getOnboardingStatus(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal
    ) {
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }
}
