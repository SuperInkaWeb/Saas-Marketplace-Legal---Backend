package com.saas.legit.module.notification.service;

import com.saas.legit.module.identity.exception.InvalidCredentialsException;
import com.saas.legit.module.identity.exception.OtpExpiredException;
import com.saas.legit.module.notification.model.OtpPurpose;
import com.saas.legit.module.notification.model.OtpVerification;
import com.saas.legit.module.notification.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_EXPIRATION_MINUTES = 10;

    private final OtpVerificationRepository otpVerificationRepository;
    private final EmailService emailService;

    @Transactional
    public void generateAndSendOtp(String email, OtpPurpose purpose) {
        String code = generateCode();

        OtpVerification otp = otpVerificationRepository.findByEmail(email)
                .orElse(new OtpVerification());

        otp.setEmail(email);
        otp.setCode(code);
        otp.setPurpose(purpose);
        otp.setExpiresAt(OffsetDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));

        otpVerificationRepository.save(otp);

        switch (purpose) {
            case ACCOUNT_VERIFICATION -> emailService.sendOtpEmail(email, code);
            case PASSWORD_RESET       -> emailService.sendPasswordResetEmail(email, code);
        }
    }

    @Transactional
    public void validateOtp(String email, String code, OtpPurpose expectedPurpose) {
        OtpVerification otp = otpVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Código OTP no encontrado o ya fue utilizado."));

        if (otp.getPurpose() != expectedPurpose) {
            throw new InvalidCredentialsException("El código no es válido para esta acción.");
        }

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now())) {
            otpVerificationRepository.delete(otp);
            throw new OtpExpiredException();
        }

        if (!otp.getCode().equals(code)) {
            throw new InvalidCredentialsException("El código OTP es incorrecto.");
        }

        otpVerificationRepository.delete(otp);
    }

    @Transactional(readOnly = true)
    public void checkOtpValidity(String email, String code, OtpPurpose expectedPurpose) {
        OtpVerification otp = otpVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Código OTP no fue encontrado o ya fue utilizado."));

        if (otp.getPurpose() != expectedPurpose) {
            throw new InvalidCredentialsException("El código no es válido para esta acción.");
        }

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new OtpExpiredException();
        }

        if (!otp.getCode().equals(code)) {
            throw new InvalidCredentialsException("El código OTP es incorrecto.");
        }
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}