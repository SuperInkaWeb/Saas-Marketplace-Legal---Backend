package com.saas.legit.module.matter.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.matter.dto.MatterCreateRequest;
import com.saas.legit.module.matter.dto.MatterResponse;
import com.saas.legit.module.matter.model.Matter;
import com.saas.legit.module.matter.repository.MatterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatterService {

    private final MatterRepository matterRepository;
    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;

    @Transactional
    public MatterResponse createMatter(Long lawyerId, MatterCreateRequest request) {
        User lawyer = userRepository.findById(lawyerId)
            .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found"));

        ClientProfile client = null;
        if (request.getClientPublicId() != null) {
            client = clientProfileRepository.findByPublicId(request.getClientPublicId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        }

        Matter matter = new Matter();
        matter.setTitle(request.getTitle());
        matter.setDescription(request.getDescription());
        matter.setJurisdiction(request.getJurisdiction());
        matter.setLawyer(lawyer);
        
        if (client != null) {
            matter.setClient(client);
        } else {
            matter.setUnregisteredClientName(request.getUnregisteredClientName());
        }

        // Simple numbering logic (Could be expanded later)
        matter.setNumber("EXP-" + System.currentTimeMillis() % 100000);

        Matter saved = matterRepository.save(matter);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MatterResponse> getMyMatters(Long lawyerId) {
        return matterRepository.findByLawyer_IdUserOrderByCreatedAtDesc(lawyerId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MatterResponse getMatter(Long lawyerId, UUID publicId) {
        Matter matter = matterRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));

        if (!matter.getLawyer().getIdUser().equals(lawyerId)) {
            throw new IllegalArgumentException("Not authorized to view this matter");
        }

        return mapToResponse(matter);
    }

    private MatterResponse mapToResponse(Matter matter) {
        return MatterResponse.builder()
                .publicId(matter.getPublicId())
                .title(matter.getTitle())
                .description(matter.getDescription())
                .number(matter.getNumber())
                .status(matter.getStatus())
                .jurisdiction(matter.getJurisdiction())
                .startDate(matter.getStartDate())
                .estimatedEndDate(matter.getEstimatedEndDate())
                .clientName(matter.getClient() != null ? matter.getClient().getCompanyName() : matter.getUnregisteredClientName())
                .clientId(matter.getClient() != null ? matter.getClient().getIdClientProfile() : null)
                .build();
    }
}
