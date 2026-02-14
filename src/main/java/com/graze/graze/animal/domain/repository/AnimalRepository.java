package com.graze.graze.animal.domain.repository;

import com.graze.graze.animal.domain.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, String> {
}
