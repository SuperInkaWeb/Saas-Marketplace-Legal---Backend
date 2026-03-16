package com.saas.legit.module.identity.service;

import com.saas.legit.module.catalog.model.LawFirm;
import com.saas.legit.module.identity.dto.AuthResponse;
import com.saas.legit.module.identity.dto.ClientRegistrationRequest;
import com.saas.legit.module.identity.dto.LawyerRegistrationRequest;
import com.saas.legit.module.identity.dto.LoginRequest;
import com.saas.legit.module.identity.exception.EmailAlreadyRegisteredException;
import com.saas.legit.module.identity.exception.InvalidCredentialsException;
import com.saas.legit.module.identity.exception.RoleNotFoundException;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.model.IdentityDocument;
import com.saas.legit.module.identity.model.Role;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.identity.repository.IdentityDocumentRepository;
import com.saas.legit.module.identity.repository.RoleRepository;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import com.saas.legit.module.notification.model.OtpPurpose;
import com.saas.legit.module.notification.service.OtpService;
import com.saas.legit.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ROLE_CLIENT = "CLIENT";
    private static final String ROLE_LAWYER = "LAWYER";

    private final OtpService otpService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final IdentityDocumentRepository identityDocumentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // REGISTRO PARA CLIENTES
    @Transactional
    public void registerClient(ClientRegistrationRequest request) {
        validateEmailNotTaken(request.email());

        User user = buildUser(request.email(), request.password(), request.firstName(), request.lastNameFather(),
                request.lastNameMother(), request.phoneNumber());
        user.getRoles().add(findRole(ROLE_CLIENT));
        userRepository.save(user);

        clientProfileRepository.save(new ClientProfile(user, request.companyName(), request.billingAddress()));

        otpService.generateAndSendOtp(user.getEmail(), OtpPurpose.ACCOUNT_VERIFICATION);
    }

    // REGISTRO PARA ABOGADOS
    @Transactional
    public void registerLawyer(LawyerRegistrationRequest request) {
        validateEmailNotTaken(request.email());

        User user = buildUser(request.email(), request.password(), request.firstName(), request.lastNameFather(),
                request.lastNameMother(), request.phoneNumber());
        user.getRoles().add(findRole(ROLE_LAWYER));
        userRepository.save(user);

        String slug = generateUniqueSlug(request.firstName(), request.lastNameFather());
        LawyerProfile profile = new LawyerProfile(user, slug, request.city(), request.country());
        lawyerProfileRepository.save(profile);

        identityDocumentRepository.save(new IdentityDocument(user, request.documentType(), request.documentNumber(),
                request.documentCountryCode()));
        otpService.generateAndSendOtp(user.getEmail(), OtpPurpose.ACCOUNT_VERIFICATION);
    }

    // LOGIN
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailWithRoles(request.email()).orElseThrow(InvalidCredentialsException::new);

        if (!user.getIsActive()) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordSecret())) {
            throw new InvalidCredentialsException();
        }

        Long tenantId = resolveTenantId(user);

        return buildAuthResponse(user, tenantId);
    }

    private void validateEmailNotTaken(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }

    @Transactional
    public AuthResponse verifyAccountOtp(String email, String code) {
        otpService.validateOtp(email, code, OtpPurpose.ACCOUNT_VERIFICATION);

        User user = userRepository.findByEmailWithRoles(email).orElseThrow();
        user.setIsActive(true);
        userRepository.save(user);

        return buildAuthResponse(user, resolveTenantId(user));
    }

    @Transactional
    public void resendOtp(String email, OtpPurpose purpose) {
        String normalizedEmail = email.toLowerCase();
        if (!userRepository.existsByEmail(normalizedEmail)) {
            return;
        }
        otpService.generateAndSendOtp(normalizedEmail, purpose);
    }

    @Transactional
    public void forgotPassword(String email) {
        String normalizedEmail = email.toLowerCase();
        if (!userRepository.existsByEmail(normalizedEmail)) {
            return;
        }
        otpService.generateAndSendOtp(normalizedEmail, OtpPurpose.PASSWORD_RESET);
    }

    @Transactional(readOnly = true)
    public void validateResetOtp(String email, String code) {
        otpService.checkOtpValidity(email, code, OtpPurpose.PASSWORD_RESET);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        otpService.validateOtp(email, code, OtpPurpose.PASSWORD_RESET);

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        user.setPasswordSecret(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private Role findRole(String roleName) {
        return roleRepository.findByNameRol(roleName).orElseThrow(() -> new RoleNotFoundException(roleName));
    }

    private User buildUser(String email, String rawPassword, String firstName, String lastNameFather,
            String lastNameMother, String phone) {
        return new User(email, passwordEncoder.encode(rawPassword), firstName, lastNameFather, lastNameMother, phone);
    }

    private Long resolveTenantId(User user) {
        boolean isLawyer = user.getRoles().stream().anyMatch(r -> ROLE_LAWYER.equals(r.getNameRol()));

        if (!isLawyer)
            return null;

        return lawyerProfileRepository.findByUserId(user.getIdUser()).map(LawyerProfile::getLawFirm)
                .map(LawFirm::getIdLawFirm).orElse(null);
    }

    private String generateUniqueSlug(String firstName, String lastName) {
        String base = Normalizer.normalize(firstName + "-" + lastName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "").toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");

        String slug = base;
        while (lawyerProfileRepository.existsBySlugLawyerProfile(slug)) {
            slug = base + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        return slug;
    }

    private AuthResponse buildAuthResponse(User user, Long tenantId) {
        List<String> roles = user.getRoles().stream().map(Role::getNameRol).toList();

        String token = jwtService.generateToken(user.getIdUser(), user.getEmail(), roles, tenantId);

        String fullName = user.getFirstName() + " " + user.getLastNameFather();

        return new AuthResponse(user.getPublicId(), user.getEmail(), fullName, roles, token);
    }
}