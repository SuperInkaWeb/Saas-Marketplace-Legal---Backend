package com.saas.legit.module.identity.controller;

import com.saas.legit.module.identity.dto.PublicProfileResponse;
import com.saas.legit.module.identity.dto.UpdateClientProfileRequest;
import com.saas.legit.module.identity.dto.UpdateLawyerProfileRequest;
import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.identity.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/client")
    public ResponseEntity<Void> updateClientProfile(@Valid @RequestBody UpdateClientProfileRequest request) {
        profileService.updateClientProfile(SecurityUtils.getCurrentUser().userId(), request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/lawyer")
    public ResponseEntity<Void> updateLawyerProfile(@Valid @RequestBody UpdateLawyerProfileRequest request) {
        profileService.updateLawyerProfile(SecurityUtils.getCurrentUser().userId(), request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String url = profileService.updateAvatar(SecurityUtils.getCurrentUser().userId(), file);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PatchMapping("/client/logo")
    public ResponseEntity<?> updateClientLogo(@RequestParam("file") MultipartFile file) throws IOException {
        String url = profileService.updateClientCompanyLogo(SecurityUtils.getCurrentUser().userId(), file);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PatchMapping("/law-firm/logo")
    public ResponseEntity<?> updateLawFirmLogo(@RequestParam("file") MultipartFile file) throws IOException {
        String url = profileService.updateLawFirmLogo(SecurityUtils.getCurrentUser().userId(), file);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PatchMapping("/law-firm/cover")
    public ResponseEntity<?> updateLawFirmCover(@RequestParam("file") MultipartFile file) throws IOException {
        String url = profileService.updateLawFirmCover(SecurityUtils.getCurrentUser().userId(), file);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/public/{slug}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable String slug) {
        return ResponseEntity.ok(profileService.getPublicProfile(slug));
    }
}
