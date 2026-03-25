// src/.../marketplace/service/ClientCaseService.java
package com.saas.legit.module.marketplace.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.marketplace.dto.CaseWithProposalsResponse;
import com.saas.legit.module.marketplace.dto.CreateCaseRequest;
import com.saas.legit.module.marketplace.dto.LawyerProposalResponse;
import com.saas.legit.module.marketplace.exception.CaseRequestNotFoundException;
import com.saas.legit.module.marketplace.exception.UnauthorizedAccessException;
import com.saas.legit.module.marketplace.model.CaseRequest;
import com.saas.legit.module.marketplace.model.CaseRequestStatus;
import com.saas.legit.module.marketplace.model.LawyerProposal;
import com.saas.legit.module.marketplace.model.ProposalStatus;
import com.saas.legit.module.marketplace.model.Specialty;
import com.saas.legit.module.marketplace.repository.CaseRequestRepository;
import com.saas.legit.module.marketplace.repository.LawyerProposalRepository;
import com.saas.legit.module.marketplace.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientCaseService {

    private final CaseRequestRepository caseRequestRepository;
    private final LawyerProposalRepository lawyerProposalRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final SpecialtyRepository specialtyRepository;

    // ── CREAR CASO ─────────────────────────────────────────────────────

    @Transactional
    public CaseWithProposalsResponse createCase(Long userId, CreateCaseRequest request) {
        ClientProfile clientProfile = clientProfileRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado"));

        CaseRequest caseRequest = new CaseRequest();
        caseRequest.setClientProfile(clientProfile);
        caseRequest.setTitle(request.title());
        caseRequest.setDescription(request.description());
        caseRequest.setBudget(request.budget());
        caseRequest.setStatus(CaseRequestStatus.OPEN);

        if (request.specialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.specialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
            caseRequest.setSpecialty(specialty);
        }

        CaseRequest saved = caseRequestRepository.save(caseRequest);
        return mapToCaseWithProposals(saved, List.of());
    }

    // ── VER MIS CASOS CON PROPUESTAS ───────────────────────────────────

    @Transactional(readOnly = true)
    public List<CaseWithProposalsResponse> getMyCases(Long userId) {
        ClientProfile clientProfile = clientProfileRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado"));

        List<CaseRequest> cases = caseRequestRepository
                .findByClientProfile_IdClientProfileOrderByCreatedAtDesc(
                        clientProfile.getIdClientProfile()
                );

        return cases.stream()
                .map(caseRequest -> {
                    List<LawyerProposal> proposals = lawyerProposalRepository
                            .findByCaseRequest_IdOrderByCreatedAtDesc(caseRequest.getId());
                    return mapToCaseWithProposals(caseRequest, proposals);
                })
                .toList();
    }

    // ── ACEPTAR PROPUESTA ──────────────────────────────────────────────

    @Transactional
    public void acceptProposal(Long userId, UUID casePublicId, Long proposalId) {
        ClientProfile clientProfile = clientProfileRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado"));

        CaseRequest caseRequest = caseRequestRepository.findByPublicId(casePublicId)
                .orElseThrow(CaseRequestNotFoundException::new);

        if (!caseRequest.getClientProfile().getIdClientProfile()
                .equals(clientProfile.getIdClientProfile())) {
            throw new UnauthorizedAccessException("caso");
        }

        if (caseRequest.getStatus() != CaseRequestStatus.OPEN) {
            throw new IllegalArgumentException("Solo se pueden aceptar propuestas en casos abiertos");
        }

        LawyerProposal proposal = lawyerProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta no encontrada"));

        if (!proposal.getCaseRequest().getId().equals(caseRequest.getId())) {
            throw new UnauthorizedAccessException("propuesta");
        }

        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new IllegalArgumentException("La propuesta ya fue procesada");
        }

        // Aceptar la propuesta seleccionada
        proposal.setStatus(ProposalStatus.ACCEPTED);
        lawyerProposalRepository.save(proposal);

        // Rechazar el resto automáticamente en una sola query
        lawyerProposalRepository.rejectOtherProposals(caseRequest.getId(), proposalId);

        // Cerrar el caso
        caseRequest.setStatus(CaseRequestStatus.IN_PROGRESS);
        caseRequestRepository.save(caseRequest);
    }

    // ── CERRAR CASO ────────────────────────────────────────────────────

    @Transactional
    public void closeCase(Long userId, UUID casePublicId) {
        ClientProfile clientProfile = clientProfileRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado"));

        CaseRequest caseRequest = caseRequestRepository.findByPublicId(casePublicId)
                .orElseThrow(CaseRequestNotFoundException::new);

        if (!caseRequest.getClientProfile().getIdClientProfile()
                .equals(clientProfile.getIdClientProfile())) {
            throw new UnauthorizedAccessException("caso");
        }

        if (caseRequest.getStatus() == CaseRequestStatus.CLOSED) {
            throw new IllegalArgumentException("El caso ya está cerrado");
        }

        caseRequest.setStatus(CaseRequestStatus.CLOSED);
        caseRequestRepository.save(caseRequest);
    }

    // ── PRIVATE HELPERS ────────────────────────────────────────────────

    private CaseWithProposalsResponse mapToCaseWithProposals(
            CaseRequest caseRequest,
            List<LawyerProposal> proposals) {

        List<LawyerProposalResponse> proposalResponses = proposals.stream()
                .map(p -> LawyerProposalResponse.builder()
                        .id(p.getId())
                        .lawyerName(p.getLawyerProfile().getUser().getFirstName()
                                + " " + p.getLawyerProfile().getUser().getLastNameFather())
                        .lawyerPublicId(p.getLawyerProfile().getPublicId().toString())
                        .proposalText(p.getProposalText())
                        .proposedFee(p.getProposedFee())
                        .status(p.getStatus())
                        .createdAt(p.getCreatedAt())
                        .build())
                .toList();

        return CaseWithProposalsResponse.builder()
                .publicId(caseRequest.getPublicId())
                .title(caseRequest.getTitle())
                .description(caseRequest.getDescription())
                .budget(caseRequest.getBudget())
                .specialtyName(caseRequest.getSpecialty() != null
                        ? caseRequest.getSpecialty().getName() : null)
                .status(caseRequest.getStatus())
                .createdAt(caseRequest.getCreatedAt())
                .proposals(proposalResponses)
                .build();
    }
}