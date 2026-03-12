package com.graze.graze.animal.application;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.domain.AnimalOwners;
import com.graze.graze.animal.domain.dto.AnimalDto;
import com.graze.graze.animal.domain.dto.AnimalOwnerDto;
import com.graze.graze.animal.domain.dto.CreateAndEditAnimalDTO;
import com.graze.graze.animal.domain.enums.AnimalType;
import com.graze.graze.animal.domain.enums.Gender;
import com.graze.graze.animal.domain.interfaces.AnimalInterface;
import com.graze.graze.animal.domain.mapper.AnimalMapper;
import com.graze.graze.animal.domain.repository.AnimalRepository;
import com.graze.graze.animal.events.AnimalRegistered;
import com.graze.graze.common.exceptions.ResourceNotFoundException;
import com.graze.graze.finances.domain.repository.FinanceRepository;
import com.graze.graze.health.domain.repository.HealthRecordRepository;
import com.graze.graze.user.application.UserService;
import com.graze.graze.user.domain.User;
import com.graze.graze.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimalService implements AnimalInterface {
  private final ApplicationEventPublisher publisher;
  private final AnimalMapper animalMapper;
  private final AnimalRepository animalRepository;
  private final HealthRecordRepository healthRecordRepository;
  private final FinanceRepository financeRepository;
  private final UserRepository userRepository;
  private final UserService userService;

  @Transactional
  public AnimalDto register(CreateAndEditAnimalDTO createAndEditAnimalDTO) {
    Animal animal = animalMapper.toAnimal(createAndEditAnimalDTO);
    resolveParents(animal, createAndEditAnimalDTO);
    Animal savedAnimal = animalRepository.save(animal);
    resolveOwners(savedAnimal, createAndEditAnimalDTO.getOwners());
    Animal result = animalRepository.save(savedAnimal);
    publisher.publishEvent(new AnimalRegistered(result.getTagNo()));
    return animalMapper.toDto(result);
  }

  public List<AnimalDto> findAll() {
    return animalMapper.toDtoList(animalRepository.findAll());
  }

  public AnimalDto findByTagNo(String tagNo) {
    Animal animal = animalRepository.findById(tagNo)
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + tagNo));
    return animalMapper.toDto(animal);
  }

  public List<AnimalDto> findByType(AnimalType type) {
    return animalMapper.toDtoList(animalRepository.findByType(type));
  }

  public List<AnimalDto> findByGender(Gender gender) {
    return animalMapper.toDtoList(animalRepository.findByGender(gender));
  }

  public List<AnimalDto> findByTypeAndGender(AnimalType type, Gender gender) {
    return animalMapper.toDtoList(animalRepository.findByTypeAndGender(type, gender));
  }

  @Transactional
  public AnimalDto update(String tagNo, CreateAndEditAnimalDTO createAndEditAnimalDTO) {
    Animal animal = animalRepository.findById(tagNo)
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + tagNo));
    animalMapper.updateAnimalFromDto(createAndEditAnimalDTO, animal);
    resolveParents(animal, createAndEditAnimalDTO);
    Animal saved = animalRepository.save(animal);
    return animalMapper.toDto(saved);
  }

  @Transactional
  public void delete(String tagNo) {
    Animal animal = animalRepository.findById(tagNo)
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + tagNo));
    healthRecordRepository.deleteByAnimalTagNo(tagNo);
    financeRepository.unlinkAnimal(tagNo);
    for (Animal child : animalRepository.findByMotherTagNo(tagNo)) {
      child.setMother(null);
      animalRepository.save(child);
    }
    for (Animal child : animalRepository.findByFatherTagNo(tagNo)) {
      child.setFather(null);
      animalRepository.save(child);
    }
    animalRepository.delete(animal);
  }

  public List<AnimalDto> findOffspring(String tagNo) {
    Animal animal = animalRepository.findById(tagNo)
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + tagNo));

    List<Animal> offspring;
    if (animal.getGender() == Gender.FEMALE) {
      offspring = animalRepository.findByMotherTagNo(tagNo);
    } else {
      offspring = animalRepository.findByFatherTagNo(tagNo);
    }
    return animalMapper.toDtoList(offspring);
  }

  private void resolveParents(Animal animal, CreateAndEditAnimalDTO dto) {
    if (dto.getMotherTagNo() != null) {
      Animal mother = animalRepository.findById(dto.getMotherTagNo())
        .orElseThrow(() -> new ResourceNotFoundException("Mother not found with tag: " + dto.getMotherTagNo()));
      animal.setMother(mother);
    }
    if (dto.getFatherTagNo() != null) {
      Animal father = animalRepository.findById(dto.getFatherTagNo())
        .orElseThrow(() -> new ResourceNotFoundException("Father not found with tag: " + dto.getFatherTagNo()));
      animal.setFather(father);
    }
  }

  private void resolveOwners(Animal animal, List<AnimalOwnerDto> ownerDtos) {
    Jwt jwt = getJwt();
    User currentUser = resolveUserFromJwt(jwt);
    String currentUserRole = extractRoleFromJwt(jwt);

    // Clear the existing managed collection instead of replacing the reference,
    // because orphanRemoval = true requires the same collection instance.
    animal.getOwners().clear();

    if (ownerDtos == null || ownerDtos.isEmpty()) {
      // Auto-assign the logged-in user as owner with their Keycloak group role
      AnimalOwners owner = new AnimalOwners();
      owner.setAnimalTagNo(animal.getTagNo());
      owner.setUserId(currentUser.getId());
      owner.setRole(currentUserRole);
      owner.setOwnedSince(LocalDate.now());
      animal.getOwners().add(owner);
      return;
    }

    for (AnimalOwnerDto dto : ownerDtos) {
      // If userId is not provided, default to the logged-in user
      UUID userId = dto.getUserId() != null ? dto.getUserId() : currentUser.getId();

      if (!userRepository.existsById(userId)) {
        throw new ResourceNotFoundException("User not found with id: " + userId);
      }

      // If role is not provided, default to the logged-in user's Keycloak group
      String role = (dto.getRole() != null && !dto.getRole().isBlank())
        ? dto.getRole()
        : currentUserRole;

      AnimalOwners owner = new AnimalOwners();
      owner.setAnimalTagNo(animal.getTagNo());
      owner.setUserId(userId);
      owner.setRole(role);
      owner.setOwnedSince(dto.getOwnedSince() != null ? dto.getOwnedSince() : LocalDate.now());
      animal.getOwners().add(owner);
    }
  }

  /**
   * Returns the JWT from the current security context.
   */
  private Jwt getJwt() {
    return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  /**
   * Resolves the local {@link User} entity from the Keycloak JWT,
   * auto-linking or creating the user if necessary.
   */
  private User resolveUserFromJwt(Jwt jwt) {
    return userService.resolveFromJwt(jwt);
  }

  /**
   * Extracts the user's group name from the Keycloak {@code groups} claim
   * (e.g. {@code ["/owner"]} → {@code "owner"}).
   */
  private String extractRoleFromJwt(Jwt jwt) {
    List<String> groups = jwt.getClaimAsStringList("groups");
    if (groups != null && !groups.isEmpty()) {
      // Groups come as full paths, e.g. "/owner" — strip the leading slash
      String group = groups.getFirst();
      return group.startsWith("/") ? group.substring(1) : group;
    }
    return "owner"; // sensible default
  }
}
