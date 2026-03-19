package com.saas.legit.module.document.repository;

import com.saas.legit.module.document.model.DocumentPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentPurchaseRepository extends JpaRepository<DocumentPurchase, Long> {
    List<DocumentPurchase> findByClientProfile_IdClientProfileOrderByPurchasedAtDesc(Long clientProfileId);
    List<DocumentPurchase> findByDocument_User_IdUserOrderByPurchasedAtDesc(Long lawyerUserId);
}
