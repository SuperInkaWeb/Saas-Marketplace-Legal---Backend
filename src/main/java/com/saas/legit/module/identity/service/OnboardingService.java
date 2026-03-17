package com.saas.legit.module.identity.service;

import com.saas.legit.module.identity.dto.CreateClientProfileRequest;
import com.saas.legit.module.identity.dto.CreateLawyerProfileRequest;
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
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private static final String ROLE_CLIENT = "CLIENT";
    private static final String ROLE_LAWYER = "LAWYER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final LawyerProfileRepository lawyerProfileRepository;

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
        requireRole(user, ROLE_CLIENT);

        if (clientProfileRepository.findByUser(user).isPresent()) {
            user.setOnboardingStep(OnboardingStep.COMPLETED);
            userRepository.save(user);
            return;
        }

        ClientProfile profile = new ClientProfile(
                user,
                request.companyName(),
                request.billingAddress()
        );
        clientProfileRepository.save(profile);

        user.setOnboardingStep(OnboardingStep.COMPLETED);
        userRepository.save(user);
    }

    // ── CREATE LAWYER PROFILE ──────────────────────────────────────────

    @Transactional
    public void createLawyerProfile(Long userId, CreateLawyerProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow();

        requireStep(user, OnboardingStep.PROFILE_PENDING);
        requireRole(user, ROLE_LAWYER);

        if (lawyerProfileRepository.findByUserId(user.getIdUser()).isPresent()) {
            user.setOnboardingStep(OnboardingStep.KYC_PENDING);
            userRepository.save(user);
            return;
        }

        String slug = generateUniqueSlug(user.getFirstName(), user.getLastNameFather());
        LawyerProfile profile = new LawyerProfile(user, slug, request.city(), request.country());
        lawyerProfileRepository.save(profile);

        user.setOnboardingStep(OnboardingStep.KYC_PENDING);
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

    private void requireRole(User user, String roleName) {
        if (!user.hasRole(roleName)) {
            throw new InvalidOnboardingStepException(
                    "ROL_INCORRECTO",
                    "El rol del usuario debe ser " + roleName
            );
        }
    }

    private String generateUniqueSlug(String firstName, String lastName) {
        String base = Normalizer.normalize(firstName + "-" + lastName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String slug = base;
        while (lawyerProfileRepository.existsBySlugLawyerProfile(slug)) {
            slug = base + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        return slug;
    }
}
