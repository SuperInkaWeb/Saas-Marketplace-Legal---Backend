package com.saas.legit.module.matter.controller;

import com.saas.legit.module.matter.dto.MatterTaskRequest;
import com.saas.legit.module.matter.dto.MatterTaskResponse;
import com.saas.legit.module.matter.service.MatterTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matters")
@RequiredArgsConstructor
public class MatterTaskController {

    private final MatterTaskService taskService;

    @PostMapping("/{matterPublicId}/tasks")
    public ResponseEntity<MatterTaskResponse> addTask(
            @PathVariable UUID matterPublicId,
            @Valid @RequestBody MatterTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.addTask(matterPublicId, request));
    }

    @GetMapping("/{matterPublicId}/tasks")
    public ResponseEntity<List<MatterTaskResponse>> getTasks(@PathVariable UUID matterPublicId) {
        return ResponseEntity.ok(taskService.getTasks(matterPublicId));
    }

    @PatchMapping("/tasks/{taskPublicId}/toggle")
    public ResponseEntity<MatterTaskResponse> toggleTask(@PathVariable UUID taskPublicId) {
        return ResponseEntity.ok(taskService.toggleTaskStatus(taskPublicId));
    }
}
