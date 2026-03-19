package com.saas.legit.module.appointment.repository;

import com.saas.legit.module.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByPublicId(UUID publicId);
    
    List<Appointment> findByClientProfile_IdClientProfileOrderByScheduledStartDesc(Long clientProfileId);
    
    List<Appointment> findByLawyerProfile_IdLawyerProfileOrderByScheduledStartDesc(Long lawyerProfileId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.lawyerProfile.idLawyerProfile = :lawyerId " +
           "AND a.status NOT IN ('CANCELLED', 'NO_SHOW') " +
           "AND ((a.scheduledStart < :end AND a.scheduledEnd > :start))")
    boolean hasOverlappingAppointments(@Param("lawyerId") Long lawyerId, 
                                       @Param("start") OffsetDateTime start, 
                                       @Param("end") OffsetDateTime end);
}
