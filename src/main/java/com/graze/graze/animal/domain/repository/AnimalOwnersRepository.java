package com.graze.graze.animal.domain.repository;

import com.graze.graze.animal.domain.AnimalOwnerId;
import com.graze.graze.animal.domain.AnimalOwners;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalOwnersRepository extends JpaRepository<AnimalOwners, AnimalOwnerId> {

    List<AnimalOwners> findByAnimalTagNo(String animalTagNo);

    void deleteByAnimalTagNo(String animalTagNo);
}

