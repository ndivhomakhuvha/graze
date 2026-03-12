package com.graze.graze.health.domain.repository;

import com.graze.graze.health.domain.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
}
