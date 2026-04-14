package com.saas.legit.module.subscription.repository;

import com.saas.legit.module.subscription.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    
    @Query("SELECT us FROM UserSubscription us JOIN FETCH us.plan WHERE us.user.idUser = :userId AND us.status = 'ACTIVE'")
    Optional<UserSubscription> findActiveSubscriptionByUserId(Long userId);
}
