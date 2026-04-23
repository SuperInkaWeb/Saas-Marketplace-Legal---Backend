package com.saas.legit.module.identity.service;

import com.saas.legit.module.client.dto.CreateClientProfileRequest;
import com.saas.legit.module.identity.dto.SelectRoleRequest;
import com.saas.legit.module.identity.exception.InvalidOnboardingStepException;
import com.saas.legit.module.identity.exception.RoleAlreadyAssignedException;
import com.saas.legit.module.identity.exception.RoleNotFoundException;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.model.OnboardingStep;
import com.saas.legit.module.identity.model.Role;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.identity.repository.RoleRepository;
import com.saas.legit.module.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private static final String ROLE_CLIENT = "CLIENT";
    private static final String ROLE_LAWYER = "LAWYER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClientProfileRepository clientProfileRepository;

    // ── SELECT ROLE ────────────────────────────────────────────────────

    @Transactional
    public void selectRole(Long userId, SelectRoleRequest request) {
        User user = userRepository.findById(userId).orElseThrow();

        requireStep(user, OnboardingStep.ROLE_SELECTION);

        if (user.getRole() != null) {
            throw new RoleAlreadyAssignedException();
        }

        String roleName = request.role().toUpperCase();
        if (!roleName.equals(ROLE_CLIENT) && !roleName.equals(ROLE_LAWYER)) {
            throw new RoleNotFoundException(roleName);
        }

        Role role = roleRepository.findByNameRol(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));

        user.setRole(role);
        user.setOnboardingStep(OnboardingStep.PROFILE_PENDING);
        userRepository.save(user);
    }

    // ── CREATE CLIENT PROFILE ──────────────────────────────────────────

    @Transactional
    public void createClientProfile(Long userId, CreateClientProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow();

        requireStep(user, OnboardingStep.PROFILE_PENDING);
        requireRole(user);

        ClientProfile profile = clientProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    ClientProfile newProfile = new ClientProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setCompanyName(request.companyName());
        profile.setBillingAddress(request.billingAddress());
        clientProfileRepository.save(profile);

        user.setOnboardingStep(OnboardingStep.COMPLETED);
        userRepository.save(user);
    }

    // ── PRIVATE HELPERS ────────────────────────────────────────────────

    private void requireStep(User user, OnboardingStep required) {
        if (user.getOnboardingStep() != required) {
            throw new InvalidOnboardingStepException(
                    user.getOnboardingStep().name(), required.name()
            );
        }
    }

    private void requireRole(User user) {
        if (!user.hasRole(OnboardingService.ROLE_CLIENT)) {
            throw new InvalidOnboardingStepException(
                    "ROL_INCORRECTO",
                    "El rol del usuario debe ser " + OnboardingService.ROLE_CLIENT
            );
        }
    }
}
