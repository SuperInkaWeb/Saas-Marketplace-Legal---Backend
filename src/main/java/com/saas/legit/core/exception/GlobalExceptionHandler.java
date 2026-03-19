package com.saas.legit.core.exception;

import com.saas.legit.module.identity.exception.*;
import com.saas.legit.module.marketplace.exception.LawyerProfileNotFoundException;
import com.saas.legit.module.marketplace.exception.ScheduleConflictException;
import com.saas.legit.module.marketplace.exception.SpecialtyNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ProblemDetail handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Registro Duplicado");
        return problem;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Acceso Denegado");
        return problem;
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ProblemDetail handleOtpExpired(OtpExpiredException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.GONE, ex.getMessage());
        problem.setTitle("Código Expirado");
        problem.setProperty("acción", "Solicita un nuevo código usando el endpoint /resend-otp");
        return problem;
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ProblemDetail handleAccountBlocked(AccountBlockedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Cuenta Bloqueada");
        return problem;
    }

    @ExceptionHandler(InvalidOnboardingStepException.class)
    public ProblemDetail handleInvalidOnboardingStep(InvalidOnboardingStepException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Paso de Onboarding Inválido");
        return problem;
    }

    @ExceptionHandler(RoleAlreadyAssignedException.class)
    public ProblemDetail handleRoleAlreadyAssigned(RoleAlreadyAssignedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Rol Ya Asignado");
        return problem;
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ProblemDetail handleRoleNotFound(RoleNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Rol No Encontrado");
        return problem;
    }

    @ExceptionHandler(KycRequiredException.class)
    public ProblemDetail handleKycRequired(KycRequiredException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Verificación Requerida");
        return problem;
    }

    @ExceptionHandler(UnauthorizedKycAccessException.class)
    public ProblemDetail handleUnauthorizedKycAccess(UnauthorizedKycAccessException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Acceso KYC No Autorizado");
        return problem;
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ProblemDetail handleTooManyRequests(TooManyRequestsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS,
                "Has excedido el número de intentos permitidos. Por favor, intenta más tarde."
        );
        problem.setTitle("Demasiadas solicitudes");
        problem.setProperty("ayuda", "Si olvidaste tu contraseña, utiliza la opción de recuperación.");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        problem.setTitle("Error de validación");
        return problem;
    }

    @ExceptionHandler(LawyerProfileNotFoundException.class)
    public ProblemDetail handleLawyerProfileNotFound(LawyerProfileNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Perfil No Encontrado");
        return problem;
    }

    @ExceptionHandler(SpecialtyNotFoundException.class)
    public ProblemDetail handleSpecialtyNotFound(SpecialtyNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Especialidad No Encontrada");
        return problem;
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ProblemDetail handleScheduleConflict(ScheduleConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflicto de Horario");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Error inesperado: {}", String.valueOf(ex));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor"
        );
        problem.setTitle("Error inesperado");
        return problem;
    }
}