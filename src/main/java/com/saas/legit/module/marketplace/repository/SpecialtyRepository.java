package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findAllByIdIn(List<Long> ids);
}
