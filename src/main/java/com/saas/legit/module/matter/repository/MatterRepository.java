package com.saas.legit.module.matter.repository;

import com.saas.legit.module.matter.model.Matter;
import com.saas.legit.module.matter.model.MatterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatterRepository extends JpaRepository<Matter, Long> {
    Optional<Matter> findByPublicId(UUID publicId);
    List<Matter> findByLawyer_IdUserOrderByCreatedAtDesc(Long lawyerId);
    List<Matter> findByClient_IdClientProfileOrderByCreatedAtDesc(Long clientId);

    @Query("SELECT m FROM Matter m WHERE m.lawyer.idUser = :lawyerId " +
           "AND (:hasSearch = false OR LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(m.number) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(m.unregisteredClientName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:hasStatus = false OR m.status = :status) " +
           "ORDER BY m.createdAt DESC")
    List<Matter> searchMatters(@Param("lawyerId") Long lawyerId,
                               @Param("search") String search,
                               @Param("hasSearch") boolean hasSearch,
                               @Param("status") MatterStatus status,
                               @Param("hasStatus") boolean hasStatus);
}
