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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
        Long tenantId = null; // resolved in future via LawyerProfile if needed

        String token = jwtService.generateToken(
                user.getIdUser(), user.getEmail(), role, tenantId
        );

        return new AuthResponse(
                user.getPublicId(),
                user.getEmail(),
                user.getFullName(),
                role,
                user.getOnboardingStep().name(),
                token
        );
    }
}