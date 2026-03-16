package com.saas.legit.module.identity.controller;

import com.saas.legit.module.identity.dto.*;
import com.saas.legit.module.identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/client")
    public ResponseEntity<String> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registro exitoso. Por favor, revisa tu correo para verificar tu cuenta.");
    }

    @PostMapping("/register/lawyer")
    public ResponseEntity<String> registerLawyer(@Valid @RequestBody LawyerRegistrationRequest request) {
        authService.registerLawyer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registro exitoso. Por favor, revisa tu correo para verificar tu cuenta.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyAccountOtp(request.email(), request.code()));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendOtp(request.email(), request.purpose());
        return ResponseEntity.ok("Si el correo está registrado, recibirás un nuevo código en breve.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ResponseEntity.ok("Si el correo está registrado, recibirás un código de recuperación en breve.");
    }

    @PostMapping("/validate-reset-otp")
    public ResponseEntity<String> validateResetOtp(@Valid @RequestBody OtpVerificationRequest request) {
        authService.validateResetOtp(request.email(), request.code());
        return ResponseEntity.ok("Código validado correctamente.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok("Contraseña actualizada correctamente. Ya puedes iniciar sesión.");
    }
}