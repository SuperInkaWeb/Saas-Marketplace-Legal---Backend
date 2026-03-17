package com.saas.legit.module.identity.controller;

import com.saas.legit.module.identity.dto.KycStatusResponse;
import com.saas.legit.module.identity.dto.UploadIdentityDocumentRequest;
import com.saas.legit.module.identity.dto.UserMeResponse;
import com.saas.legit.module.identity.service.KycService;
import com.saas.legit.module.identity.service.UserService;
import com.saas.legit.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;
    private final UserService userService;

    @PostMapping("/upload-document")
    public ResponseEntity<UserMeResponse> uploadDocument(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal,
            @Valid @RequestBody UploadIdentityDocumentRequest request
    ) {
        kycService.uploadDocument(principal.userId(), request);
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }

    @GetMapping("/status")
    public ResponseEntity<KycStatusResponse> getKycStatus(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal
    ) {
        return ResponseEntity.ok(kycService.getKycStatus(principal.userId()));
    }
}
