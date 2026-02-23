package com.graze.graze.health.application;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.domain.repository.AnimalRepository;
import com.graze.graze.common.exceptions.ResourceNotFoundException;
import com.graze.graze.health.domain.HealthRecord;
import com.graze.graze.health.domain.Treatment;
import com.graze.graze.health.domain.dto.HealthRecordDto;
import com.graze.graze.health.domain.dto.TreatmentDto;
import com.graze.graze.health.domain.enums.RecordStatus;
import com.graze.graze.health.domain.mapper.HealthRecordMapper;
import com.graze.graze.health.domain.mapper.TreatmentMapper;
import com.graze.graze.health.domain.repository.HealthRecordRepository;
import com.graze.graze.health.domain.repository.TreatmentRepository;
import com.graze.graze.health.events.HealthRecordCreated;
import com.graze.graze.health.events.TreatmentAdministered;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class HealthService {
  private final HealthRecordRepository healthRecordRepository;
  private final TreatmentRepository treatmentRepository;
  private final AnimalRepository animalRepository;
  private final HealthRecordMapper healthRecordMapper;
  private final TreatmentMapper treatmentMapper;
  private final ApplicationEventPublisher publisher;

  public HealthService(HealthRecordRepository healthRecordRepository,
                       TreatmentRepository treatmentRepository,
                       AnimalRepository animalRepository,
                       HealthRecordMapper healthRecordMapper,
                       TreatmentMapper treatmentMapper,
                       ApplicationEventPublisher publisher) {
    this.healthRecordRepository = healthRecordRepository;
    this.treatmentRepository = treatmentRepository;
    this.animalRepository = animalRepository;
    this.healthRecordMapper = healthRecordMapper;
    this.treatmentMapper = treatmentMapper;
    this.publisher = publisher;
  }

  // ── Health Record CRUD ──

  @Transactional
  public HealthRecordDto createRecord(HealthRecordDto dto) {
    Animal animal = animalRepository.findById(dto.getAnimalTagNo())
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + dto.getAnimalTagNo()));

    HealthRecord record = healthRecordMapper.toEntity(dto);
    record.setAnimal(animal);
    record.setStatus(RecordStatus.NEW);

    if (dto.getDate() == null) {
      record.setDate(LocalDate.now());
    }

    if (dto.getTreatmentId() != null) {
      Treatment treatment = treatmentRepository.findById(dto.getTreatmentId())
        .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + dto.getTreatmentId()));
      record.setTreatment(treatment);

      if (dto.getNextDue() == null && treatment.getDefaultIntervalDays() > 0) {
        record.setNextDue(record.getDate().plusDays(treatment.getDefaultIntervalDays()));
      }
    }

    HealthRecord saved = healthRecordRepository.save(record);

    publisher.publishEvent(new HealthRecordCreated(
      saved.getId(),
      saved.getAnimal().getTagNo(),
      saved.getTreatment() != null ? saved.getTreatment().getName() : null,
      saved.getDosage()
    ));

    return healthRecordMapper.toDto(saved);
  }

  /**
   * Called by the AnimalRegistered event listener to create an initial health record.
   */
  @Transactional
  public void createInitialRecord(String animalTagNo) {
    Animal animal = animalRepository.findById(animalTagNo)
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + animalTagNo));

    HealthRecord record = new HealthRecord();
    record.setAnimal(animal);
    record.setDate(LocalDate.now());
    record.setStatus(RecordStatus.NEW);
    record.setNotes("Initial health record created upon animal registration.");

    healthRecordRepository.save(record);
  }

  public HealthRecordDto findRecordById(Long id) {
    HealthRecord record = healthRecordRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Health record not found with id: " + id));
    return healthRecordMapper.toDto(record);
  }

  public List<HealthRecordDto> findAllRecords() {
    return healthRecordMapper.toDtoList(healthRecordRepository.findAll());
  }

  public List<HealthRecordDto> findRecordsByAnimal(String animalTagNo) {
    return healthRecordMapper.toDtoList(healthRecordRepository.findByAnimalTagNo(animalTagNo));
  }

  public List<HealthRecordDto> findRecordsByStatus(RecordStatus status) {
    return healthRecordMapper.toDtoList(healthRecordRepository.findByStatus(status));
  }

  public List<HealthRecordDto> findOverdueRecords() {
    return healthRecordMapper.toDtoList(healthRecordRepository.findByNextDueBefore(LocalDate.now()));
  }

  public List<HealthRecordDto> findUpcomingRecords(int daysAhead) {
    LocalDate from = LocalDate.now();
    LocalDate to = from.plusDays(daysAhead);
    return healthRecordMapper.toDtoList(healthRecordRepository.findByNextDueBetween(from, to));
  }

  @Transactional
  public HealthRecordDto updateRecord(Long id, HealthRecordDto dto) {
    HealthRecord record = healthRecordRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Health record not found with id: " + id));

    healthRecordMapper.updateFromDto(dto, record);

    if (dto.getTreatmentId() != null) {
      Treatment treatment = treatmentRepository.findById(dto.getTreatmentId())
        .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + dto.getTreatmentId()));
      record.setTreatment(treatment);
    }

    if (dto.getStatus() != null) {
      record.setStatus(dto.getStatus());
    }

    HealthRecord saved = healthRecordRepository.save(record);
    return healthRecordMapper.toDto(saved);
  }

  @Transactional
  public HealthRecordDto completeRecord(Long id) {
    HealthRecord record = healthRecordRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Health record not found with id: " + id));

    record.setStatus(RecordStatus.COMPLETED);
    HealthRecord saved = healthRecordRepository.save(record);

    if (saved.getTreatment() != null) {
      publisher.publishEvent(new TreatmentAdministered(
        saved.getId(),
        saved.getAnimal().getTagNo(),
        saved.getTreatment().getName(),
        saved.getDosage(),
        BigDecimal.ZERO // cost can be enriched later
      ));
    }

    return healthRecordMapper.toDto(saved);
  }

  @Transactional
  public void deleteRecord(Long id) {
    if (!healthRecordRepository.existsById(id)) {
      throw new ResourceNotFoundException("Health record not found with id: " + id);
    }
    healthRecordRepository.deleteById(id);
  }

  // ── Treatment CRUD ──

  @Transactional
  public TreatmentDto createTreatment(TreatmentDto dto) {
    Treatment treatment = treatmentMapper.toEntity(dto);
    Treatment saved = treatmentRepository.save(treatment);
    return treatmentMapper.toDto(saved);
  }

  public List<TreatmentDto> findAllTreatments() {
    return treatmentMapper.toDtoList(treatmentRepository.findAll());
  }

  public TreatmentDto findTreatmentById(Long id) {
    Treatment treatment = treatmentRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + id));
    return treatmentMapper.toDto(treatment);
  }

  @Transactional
  public TreatmentDto updateTreatment(Long id, TreatmentDto dto) {
    Treatment treatment = treatmentRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + id));
    treatmentMapper.updateFromDto(dto, treatment);
    Treatment saved = treatmentRepository.save(treatment);
    return treatmentMapper.toDto(saved);
  }

  @Transactional
  public void deleteTreatment(Long id) {
    if (!treatmentRepository.existsById(id)) {
      throw new ResourceNotFoundException("Treatment not found with id: " + id);
    }
    treatmentRepository.deleteById(id);
  }
}
