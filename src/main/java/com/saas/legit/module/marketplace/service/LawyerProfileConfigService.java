package com.saas.legit.module.marketplace.service;


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

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LawyerProfileConfigService {

    private final UserRepository userRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final LawyerScheduleRepository lawyerScheduleRepository;
    private final SpecialtyRepository specialtyRepository;


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
