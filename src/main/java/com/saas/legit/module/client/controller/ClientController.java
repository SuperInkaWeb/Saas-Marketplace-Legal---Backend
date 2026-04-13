package com.saas.legit.module.client.controller;

import com.saas.legit.module.client.dto.ClientSearchResponse;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientProfileRepository clientProfileRepository;

    @GetMapping("/search")
    @PreAuthorize("hasRole('LAWYER')") // Only lawyers should search for clients to link matters
    public ResponseEntity<List<ClientSearchResponse>> searchClients(@RequestParam("q") String query) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        List<ClientSearchResponse> results = clientProfileRepository.searchClients(query.trim())
                .stream()
                .map(client -> ClientSearchResponse.builder()
                        .publicId(client.getPublicId())
                        .fullName(client.getUser().getFullName())
                        .email(client.getUser().getEmail())
                        .companyName(client.getCompanyName())
                        .avatarUrl(client.getUser().getAvatarURL())
                        .build())
                .limit(10) // Restrict response size
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }
}
