package com.saas.legit.module.marketplace.service;


import com.saas.legit.module.identity.exception.InvalidOnboardingStepException;
import com.saas.legit.module.identity.model.OnboardingStep;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.dto.*;
import com.saas.legit.module.marketplace.exception.LawyerProfileNotFoundException;
import com.saas.legit.module.marketplace.exception.ScheduleConflictException;
import com.saas.legit.module.marketplace.exception.SpecialtyNotFoundException;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.model.LawyerSchedule;
import com.saas.legit.module.marketplace.model.Specialty;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import com.saas.legit.module.marketplace.repository.LawyerScheduleRepository;
import com.saas.legit.module.marketplace.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LawyerProfileConfigService {

    private static final String ROLE_LAWYER = "LAWYER";
    private final UserRepository userRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final LawyerScheduleRepository lawyerScheduleRepository;
    private final SpecialtyRepository specialtyRepository;

    // ── CREATE LAWYER PROFILE ──────────────────────────────────────────

    @Transactional
    public void createLawyerProfile(Long userId, CreateLawyerProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow();

        requireStep(user);
        requireRole(user);

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


    // ── GET FULL CONFIG ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public LawyerProfileConfigResponse getMyProfileConfig(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(LawyerProfileNotFoundException::new);


        List<LawyerProfileConfigResponse.SpecialtyInfo> specialtyInfos = profile.getSpecialties().stream()
                .map(s -> new LawyerProfileConfigResponse.SpecialtyInfo(s.getId(), s.getName(), s.getDescription()))
                .toList();

        List<LawyerProfileConfigResponse.ScheduleInfo> scheduleInfos = profile.getSchedules().stream()
                .map(s -> new LawyerProfileConfigResponse.ScheduleInfo(
                        s.getId(),
                        s.getDayOfWeek(),
                        s.getStartTime().toString(),
                        s.getEndTime().toString(),
                        s.getIsActive()))
                .toList();

        return new LawyerProfileConfigResponse(
                user.getFirstName(),
                user.getLastNameFather(),
                user.getLastNameMother(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getAvatarURL(),
                profile.getSlugLawyerProfile(),
                profile.getBioLawyer(),
                profile.getCity(),
                profile.getCountry(),
                profile.getLatitude(),
                profile.getLongitude(),
                profile.getHourlyRate(),
                profile.getCurrency(),
                profile.getBarRegistrationNumber(),
                profile.getBarAssociation(),
                profile.getVerificationStatus().name(),
                profile.getIsVerified(),
                profile.getRatingAvg(),
                profile.getReviewCount(),

                specialtyInfos,
                scheduleInfos
        );
    }

    // ── SPECIALTIES ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAllSpecialties() {
        return specialtyRepository.findAll().stream()
                .map(s -> new SpecialtyResponse(s.getId(), s.getName(), s.getDescription()))
                .toList();
    }

    @Transactional
    public void updateSpecialties(Long userId, UpdateSpecialtiesRequest request) {
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(LawyerProfileNotFoundException::new);

        List<Specialty> specialties = specialtyRepository.findAllByIdIn(request.specialtyIds());

        if (specialties.size() != request.specialtyIds().size()) {
            List<Long> foundIds = specialties.stream().map(Specialty::getId).toList();
            List<Long> missingIds = request.specialtyIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new SpecialtyNotFoundException(missingIds);
        }

        profile.setSpecialties(new HashSet<>(specialties));
        lawyerProfileRepository.save(profile);
    }

    // ── SCHEDULES ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getMySchedules(Long userId) {
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(LawyerProfileNotFoundException::new);

        return lawyerScheduleRepository
                .findByLawyerProfileOrderByDayOfWeekAscStartTimeAsc(profile)
                .stream()
                .map(this::toScheduleResponse)
                .toList();
    }

    @Transactional
    public ScheduleResponse addSchedule(Long userId, ScheduleRequest request) {
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(LawyerProfileNotFoundException::new);

        LawyerSchedule schedule = new LawyerSchedule();
        schedule.setLawyerProfile(profile);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(LocalTime.parse(request.startTime()));
        schedule.setEndTime(LocalTime.parse(request.endTime()));
        schedule.setIsActive(true);

        try {
            schedule = lawyerScheduleRepository.save(schedule);
        } catch (DataIntegrityViolationException ex) {
            throw new ScheduleConflictException();
        }

        return toScheduleResponse(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleRequest request) {
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(LawyerProfileNotFoundException::new);

        LawyerSchedule schedule = lawyerScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (!schedule.getLawyerProfile().getIdLawyerProfile().equals(profile.getIdLawyerProfile())) {
            throw new RuntimeException("No tienes permiso para modificar este horario");
        }

        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(LocalTime.parse(request.startTime()));
        schedule.setEndTime(LocalTime.parse(request.endTime()));

        try {
            schedule = lawyerScheduleRepository.save(schedule);
        } catch (DataIntegrityViolationException ex) {
            throw new ScheduleConflictException();
        }

        return toScheduleResponse(schedule);
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(LawyerProfileNotFoundException::new);

        LawyerSchedule schedule = lawyerScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (!schedule.getLawyerProfile().getIdLawyerProfile().equals(profile.getIdLawyerProfile())) {
            throw new RuntimeException("No tienes permiso para eliminar este horario");
        }

        lawyerScheduleRepository.delete(schedule);
    }


    // ── PRIVATE HELPERS ────────────────────────────────────────────────
    private void requireStep(User user) {
        if (user.getOnboardingStep() != OnboardingStep.PROFILE_PENDING) {
            throw new InvalidOnboardingStepException(
                    user.getOnboardingStep().name(), OnboardingStep.PROFILE_PENDING.name()
            );
        }
    }

    private void requireRole(User user) {
        if (!user.hasRole(LawyerProfileConfigService.ROLE_LAWYER)) {
            throw new InvalidOnboardingStepException(
                    "ROL_INCORRECTO",
                    "El rol del usuario debe ser " + LawyerProfileConfigService.ROLE_LAWYER
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

    private ScheduleResponse toScheduleResponse(LawyerSchedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime().toString(),
                schedule.getEndTime().toString(),
                schedule.getIsActive()
        );
    }
}
