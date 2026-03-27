package com.saas.legit.module.marketplace.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.marketplace.dto.CaseRequestResponse;
import com.saas.legit.module.marketplace.dto.CaseWithProposalsResponse;
import com.saas.legit.module.marketplace.dto.CreateProposalRequest;
import com.saas.legit.module.marketplace.dto.LawyerProposalResponse;
import com.saas.legit.module.marketplace.exception.DuplicateProposalException;
import com.saas.legit.module.marketplace.model.CaseRequest;
import com.saas.legit.module.marketplace.model.CaseRequestStatus;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.model.LawyerProposal;
import com.saas.legit.module.marketplace.model.ProposalStatus;
import com.saas.legit.module.marketplace.repository.CaseRequestRepository;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import com.saas.legit.module.marketplace.repository.LawyerProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketplaceService {
    private final CaseRequestRepository caseRequestRepository;
    private final LawyerProposalRepository lawyerProposalRepository;
    private final LawyerProfileRepository lawyerProfileRepository;

    @Transactional(readOnly = true)
    public List<CaseRequestResponse> getOpenRequests() {
        return caseRequestRepository.findByStatusOrderByCreatedAtDesc(CaseRequestStatus.OPEN)
                .stream().map(this::mapToCaseResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CaseWithProposalsResponse getCaseWithProposals(UUID publicId) {
        CaseRequest caseRequest = caseRequestRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Caso no encontrado"));

        List<LawyerProposal> proposals = lawyerProposalRepository
                .findByCaseRequest_IdOrderByCreatedAtDesc(caseRequest.getId());

        List<LawyerProposalResponse> proposalResponses = proposals.stream()
                .map(this::mapToProposalResponse)
                .collect(Collectors.toList());

        return CaseWithProposalsResponse.builder()
                .publicId(caseRequest.getPublicId())
                .title(caseRequest.getTitle())
                .description(caseRequest.getDescription())
                .budget(caseRequest.getBudget())
                .specialtyName(caseRequest.getSpecialty() != null
                        ? caseRequest.getSpecialty().getName() : null)
                .clientName(caseRequest.getClientProfile().getCompanyName() != null ? 
                        caseRequest.getClientProfile().getCompanyName() : 
                        caseRequest.getClientProfile().getUser().getFullName())
                .clientAvatarUrl(caseRequest.getClientProfile().getUser().getAvatarURL())
                .status(caseRequest.getStatus())
                .createdAt(caseRequest.getCreatedAt())
                .proposals(proposalResponses)
                .build();
    }

    @Transactional
    public LawyerProposalResponse submitProposal(Long userId, UUID casePublicId, CreateProposalRequest request) {
        LawyerProfile lawyer = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found"));

        CaseRequest caseRequest = caseRequestRepository.findByPublicId(casePublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Case request not found"));

        if (caseRequest.getStatus() != CaseRequestStatus.OPEN) {
            throw new IllegalArgumentException("Case request is no longer open");
        }

        // Evitar propuestas de abogados no verificados
        if (!Boolean.TRUE.equals(lawyer.getIsVerified())) {
            throw new IllegalStateException("Solo los abogados verificados pueden enviar propuestas");
        }

        // Evitar propuestas duplicadas
        lawyerProposalRepository.findByCaseRequest_IdAndLawyerProfile_IdLawyerProfile(caseRequest.getId(), lawyer.getIdLawyerProfile())
                .ifPresent(p -> { throw new IllegalStateException("Ya has enviado una propuesta para este caso"); });

        LawyerProposal proposal = new LawyerProposal();
        proposal.setCaseRequest(caseRequest);
        proposal.setLawyerProfile(lawyer);
        proposal.setProposalText(request.getProposalText());
        proposal.setProposedFee(request.getProposedFee());
        proposal.setStatus(ProposalStatus.PENDING);

        LawyerProposal saved = lawyerProposalRepository.save(proposal);
        return mapToProposalResponse(saved);
    }

    private CaseRequestResponse mapToCaseResponse(CaseRequest caseRequest) {
        return CaseRequestResponse.builder()
                .publicId(caseRequest.getPublicId())
                .clientName(caseRequest.getClientProfile().getCompanyName() != null ? 
                            caseRequest.getClientProfile().getCompanyName() : 
                            caseRequest.getClientProfile().getUser().getFullName())
                .clientAvatarUrl(caseRequest.getClientProfile().getUser().getAvatarURL())
                .title(caseRequest.getTitle())
                .description(caseRequest.getDescription())
                .budget(caseRequest.getBudget())
                .specialtyName(caseRequest.getSpecialty() != null ? caseRequest.getSpecialty().getName() : null)
                .status(caseRequest.getStatus())
                .createdAt(caseRequest.getCreatedAt())
                .build();
    }

    private LawyerProposalResponse mapToProposalResponse(LawyerProposal proposal) {
        return LawyerProposalResponse.builder()
                .id(proposal.getId())
                .lawyerName(proposal.getLawyerProfile().getUser().getFirstName() + " " + proposal.getLawyerProfile().getUser().getLastNameFather())
                .lawyerPublicId(proposal.getLawyerProfile().getPublicId().toString())
                .lawyerSlug(proposal.getLawyerProfile().getSlugLawyerProfile())
                .proposalText(proposal.getProposalText())
                .proposedFee(proposal.getProposedFee())
                .status(proposal.getStatus())
                .lawyerAvatarUrl(proposal.getLawyerProfile().getUser().getAvatarURL())
                .createdAt(proposal.getCreatedAt())
                .build();
    }
}
