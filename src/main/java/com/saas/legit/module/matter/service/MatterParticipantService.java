package com.saas.legit.module.matter.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.matter.dto.MatterParticipantRequest;
import com.saas.legit.module.matter.dto.MatterParticipantResponse;
import com.saas.legit.module.matter.model.Matter;
import com.saas.legit.module.matter.model.MatterParticipant;
import com.saas.legit.module.matter.repository.MatterParticipantRepository;
import com.saas.legit.module.matter.repository.MatterRepository;
import com.saas.legit.module.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatterParticipantService {

    private final MatterParticipantRepository participantRepository;
    private final MatterRepository matterRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public MatterParticipantResponse addParticipant(UUID matterPublicId, MatterParticipantRequest request, Long userId) {
        Matter matter = matterRepository.findByPublicId(matterPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));

        MatterParticipant participant = new MatterParticipant();
        participant.setMatter(matter);
        participant.setFullName(request.getFullName());
        participant.setRole(request.getRole());
        participant.setEmail(request.getEmail());
        participant.setPhone(request.getPhone());
        participant.setFirmOrInstitution(request.getFirmOrInstitution());
        participant.setProfessionalId(request.getProfessionalId());
        participant.setNotes(request.getNotes());

        MatterParticipant saved = participantRepository.save(participant);

        auditLogService.log("MATTER", matter.getId(), "PARTICIPANT_ADDED", userId,
                null, saved.getFullName(),
                "Added " + saved.getRole() + ": " + saved.getFullName());

        return mapToResponse(saved);
    }

    @Transactional
    public MatterParticipantResponse updateParticipant(UUID participantPublicId, MatterParticipantRequest request) {
        MatterParticipant participant = participantRepository.findByPublicId(participantPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setFullName(request.getFullName());
        participant.setRole(request.getRole());
        participant.setEmail(request.getEmail());
        participant.setPhone(request.getPhone());
        participant.setFirmOrInstitution(request.getFirmOrInstitution());
        participant.setProfessionalId(request.getProfessionalId());
        participant.setNotes(request.getNotes());

        MatterParticipant saved = participantRepository.save(participant);
        return mapToResponse(saved);
    }

    @Transactional
    public void removeParticipant(UUID participantPublicId, Long userId) {
        MatterParticipant participant = participantRepository.findByPublicId(participantPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        auditLogService.log("MATTER", participant.getMatter().getId(), "PARTICIPANT_REMOVED", userId,
                participant.getFullName(), null,
                "Removed " + participant.getRole() + ": " + participant.getFullName());

        // Soft delete
        participant.setDeletedAt(OffsetDateTime.now());
        participantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public List<MatterParticipantResponse> getParticipants(UUID matterPublicId) {
        Matter matter = matterRepository.findByPublicId(matterPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));

        return participantRepository.findByMatter_IdOrderByCreatedAtDesc(matter.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MatterParticipantResponse mapToResponse(MatterParticipant p) {
        return MatterParticipantResponse.builder()
                .publicId(p.getPublicId())
                .fullName(p.getFullName())
                .role(p.getRole())
                .email(p.getEmail())
                .phone(p.getPhone())
                .firmOrInstitution(p.getFirmOrInstitution())
                .professionalId(p.getProfessionalId())
                .notes(p.getNotes())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
