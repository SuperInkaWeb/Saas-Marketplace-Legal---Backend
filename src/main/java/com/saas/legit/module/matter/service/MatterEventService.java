package com.saas.legit.module.matter.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.document.model.Document;
import com.saas.legit.module.document.repository.DocumentRepository;
import com.saas.legit.module.matter.dto.MatterEventRequest;
import com.saas.legit.module.matter.dto.MatterEventResponse;
import com.saas.legit.module.matter.model.Matter;
import com.saas.legit.module.matter.model.MatterEvent;
import com.saas.legit.module.matter.repository.MatterEventRepository;
import com.saas.legit.module.matter.repository.MatterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatterEventService {

    private final MatterEventRepository eventRepository;
    private final MatterRepository matterRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public MatterEventResponse addEvent(UUID matterPublicId, MatterEventRequest request) {
        Matter matter = matterRepository.findByPublicId(matterPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));

        MatterEvent event = new MatterEvent();
        event.setMatter(matter);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventType(request.getEventType());
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }

        if (request.getDocumentPublicId() != null) {
            Document document = documentRepository.findByPublicId(request.getDocumentPublicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
            event.setDocument(document);
        }

        MatterEvent saved = eventRepository.save(event);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MatterEventResponse> getEvents(UUID matterPublicId) {
        Matter matter = matterRepository.findByPublicId(matterPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));

        return eventRepository.findByMatter_IdOrderByEventDateDesc(matter.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MatterEventResponse mapToResponse(MatterEvent event) {
        return MatterEventResponse.builder()
                .publicId(event.getPublicId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventType(event.getEventType())
                .eventDate(event.getEventDate())
                .createdAt(event.getCreatedAt())
                .documentPublicId(event.getDocument() != null ? event.getDocument().getPublicId() : null)
                .documentName(event.getDocument() != null ? event.getDocument().getFileName() : null)
                .build();
    }
}
