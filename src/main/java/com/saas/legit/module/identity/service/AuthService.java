package com.saas.legit.module.identity.service;

import com.saas.legit.module.identity.dto.AuthResponse;
import com.saas.legit.module.identity.dto.LoginRequest;
import com.saas.legit.module.identity.dto.RegisterRequest;
import com.saas.legit.module.identity.exception.AccountBlockedException;
import com.saas.legit.module.identity.exception.EmailAlreadyRegisteredException;
import com.saas.legit.module.identity.exception.InvalidCredentialsException;
import com.saas.legit.module.identity.model.AccountStatus;
import com.saas.legit.module.identity.model.OnboardingStep;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.notification.model.OtpPurpose;
import com.saas.legit.module.notification.service.OtpService;
import com.saas.legit.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final com.saas.legit.module.identity.repository.ClientProfileRepository clientProfileRepository;
    private final com.saas.legit.module.marketplace.repository.LawyerProfileRepository lawyerProfileRepository;

    // ── REGISTER (unified, no roles, no profiles) ──────────────────────

    @Transactional
    public void register(RegisterRequest request) {
        String email = request.email().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        User user = new User(
                email,
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastNameFather(),
                request.lastNameMother(),
                request.phoneNumber()
        );

        userRepository.save(user);
        otpService.generateAndSendOtp(user.getEmail(), OtpPurpose.ACCOUNT_VERIFICATION);
    }

    // ── VERIFY OTP ─────────────────────────────────────────────────────

    @Transactional
    public AuthResponse verifyAccountOtp(String email, String code) {
        String normalizedEmail = email.toLowerCase();
        otpService.validateOtp(normalizedEmail, code, OtpPurpose.ACCOUNT_VERIFICATION);

        User user = userRepository.findByEmailWithRole(normalizedEmail).orElseThrow();
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setOnboardingStep(OnboardingStep.ROLE_SELECTION);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    // ── LOGIN ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase();
        User user = userRepository.findByEmailWithRole(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getAccountStatus() == AccountStatus.BLOCKED) {
            throw new AccountBlockedException();
        }

        if (user.getAccountStatus() == AccountStatus.PENDING) {
            throw new InvalidCredentialsException("Tu cuenta aún no ha sido verificada. Revisa tu correo.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordSecret())) {
            throw new InvalidCredentialsException();
        }

        return buildAuthResponse(user);
    }

    // ── PASSWORD RECOVERY ──────────────────────────────────────────────

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
        otpService.checkOtpValidity(email.toLowerCase(), code, OtpPurpose.PASSWORD_RESET);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        String normalizedEmail = email.toLowerCase();
        otpService.validateOtp(normalizedEmail, code, OtpPurpose.PASSWORD_RESET);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        user.setPasswordSecret(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ── PRIVATE HELPERS ────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user) {
        String role = user.getRoleName();
        Long tenantId = null;

        String companyName = null;
        String billingAddress = null;
        String companyLogoUrl = null;
        String bio = null;
        String city = null;
        String country = null;
        BigDecimal hourlyRate = null;
        String currency = null;
        String barRegistrationNumber = null;
        String barAssociation = null;
        String lawFirmLogoUrl = null;
        String lawFirmCoverUrl = null;
        String slug = null;

        if ("CLIENT".equals(role)) {
            var profile = clientProfileRepository.findByUser(user);
            if (profile.isPresent()) {
                companyName = profile.get().getCompanyName();
                billingAddress = profile.get().getBillingAddress();
                companyLogoUrl = profile.get().getCompanyURL();
            }
        } else if ("LAWYER".equals(role)) {
            var profile = lawyerProfileRepository.findByUserId(user.getIdUser());
            if (profile.isPresent()) {
                bio = profile.get().getBioLawyer();
                city = profile.get().getCity();
                country = profile.get().getCountry();
                hourlyRate = profile.get().getHourlyRate();
                currency = profile.get().getCurrency();
                barRegistrationNumber = profile.get().getBarRegistrationNumber();
                barAssociation = profile.get().getBarAssociation();
                slug = profile.get().getSlugLawyerProfile();

                var lawFirm = profile.get().getLawFirm();
                if (lawFirm != null) {
                    lawFirmLogoUrl = lawFirm.getLogoUrl();
                    lawFirmCoverUrl = lawFirm.getCoverPhotoUrl();
                }
            }
        }

        String token = jwtService.generateToken(
                user.getIdUser(), user.getEmail(), role, tenantId
        );

        return new AuthResponse(
                user.getPublicId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastNameFather(),
                user.getLastNameMother(),
                user.getPhoneNumber(),
                slug,
                user.getFullName(),
                role,
                user.getOnboardingStep().name(),
                token,
                user.getAvatarURL(),
                companyName,
                billingAddress,
                companyLogoUrl,
                bio,
                city,
                country,
                hourlyRate,
                currency,
                barRegistrationNumber,
                barAssociation,
                lawFirmLogoUrl,
                lawFirmCoverUrl
        );
    }
}