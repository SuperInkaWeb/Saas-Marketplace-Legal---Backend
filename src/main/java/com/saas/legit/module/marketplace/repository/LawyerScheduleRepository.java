package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.model.LawyerSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LawyerScheduleRepository extends JpaRepository<LawyerSchedule, Long> {
    List<LawyerSchedule> findByLawyerProfileIdLawyerProfileOrderByDayOfWeekAscStartTimeAsc(Long lawyerProfileId);

    List<LawyerSchedule> findByLawyerProfileOrderByDayOfWeekAscStartTimeAsc(LawyerProfile lawyerProfile);

    void deleteAllByLawyerProfile(LawyerProfile lawyerProfile);
}
