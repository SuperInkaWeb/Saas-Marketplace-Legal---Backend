package com.saas.legit.module.matter.controller;

import com.saas.legit.module.matter.dto.MatterEventRequest;
import com.saas.legit.module.matter.dto.MatterEventResponse;
import com.saas.legit.module.matter.service.MatterEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matters/{matterPublicId}/events")
@RequiredArgsConstructor
public class MatterEventController {

    private final MatterEventService eventService;

    @PostMapping
    public ResponseEntity<MatterEventResponse> addEvent(
            @PathVariable UUID matterPublicId,
            @Valid @RequestBody MatterEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEvent(matterPublicId, request));
    }

    @GetMapping
    public ResponseEntity<List<MatterEventResponse>> getEvents(@PathVariable UUID matterPublicId) {
        return ResponseEntity.ok(eventService.getEvents(matterPublicId));
    }
}
