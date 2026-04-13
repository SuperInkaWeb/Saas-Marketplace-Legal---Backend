package com.saas.legit.module.matter.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.matter.dto.MatterTaskRequest;
import com.saas.legit.module.matter.dto.MatterTaskResponse;
import com.saas.legit.module.matter.model.Matter;
import com.saas.legit.module.matter.model.MatterTask;
import com.saas.legit.module.matter.model.MatterTaskStatus;
import com.saas.legit.module.matter.repository.MatterRepository;
import com.saas.legit.module.matter.repository.MatterTaskRepository;
import com.saas.legit.module.notification.model.NotificationType;
import com.saas.legit.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatterTaskService {

    private final MatterTaskRepository taskRepository;
    private final MatterRepository matterRepository;
    private final NotificationService notificationService;

    @Transactional
    public MatterTaskResponse addTask(UUID matterPublicId, MatterTaskRequest request) {
        Matter matter = matterRepository.findByPublicId(matterPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));

        MatterTask task = new MatterTask();
        task.setMatter(matter);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setStatus(MatterTaskStatus.TODO);

        MatterTask saved = taskRepository.save(task);

        // Simple notification for the lawyer
        notificationService.createNotification(
                matter.getLawyer(),
                NotificationType.INFO,
                "Nueva Tarea Asignada",
                "Se ha creado la tarea: " + task.getTitle() + " para el expediente " + matter.getNumber(),
                "/dashboard/matters/" + matter.getPublicId()
        );

        return mapToResponse(saved);
    }

    @Transactional
    public MatterTaskResponse toggleTaskStatus(UUID publicId) {
        MatterTask task = taskRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (task.getStatus() == MatterTaskStatus.DONE) {
            task.setStatus(MatterTaskStatus.TODO);
            task.setCompletedAt(null);
        } else {
            task.setStatus(MatterTaskStatus.DONE);
            task.setCompletedAt(OffsetDateTime.now());
        }

        MatterTask updated = taskRepository.save(task);
        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<MatterTaskResponse> getTasks(UUID matterPublicId) {
        Matter matter = matterRepository.findByPublicId(matterPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));

        return taskRepository.findByMatter_IdOrderByDueDateAsc(matter.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MatterTaskResponse mapToResponse(MatterTask task) {
        return MatterTaskResponse.builder()
                .publicId(task.getPublicId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .completedAt(task.getCompletedAt())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
