package com.saas.legit.module.catalog.repository;

import com.saas.legit.module.catalog.model.LawFirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawFirmRepository extends JpaRepository<LawFirm, Long> {
}
