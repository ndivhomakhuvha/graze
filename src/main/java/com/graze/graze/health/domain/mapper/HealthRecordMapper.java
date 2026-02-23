package com.graze.graze.health.domain.mapper;

import com.graze.graze.health.domain.HealthRecord;
import com.graze.graze.health.domain.dto.HealthRecordDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HealthRecordMapper {

  @Mapping(source = "animal.tagNo", target = "animalTagNo")
  @Mapping(source = "treatment.id", target = "treatmentId")
  @Mapping(source = "treatment.name", target = "treatmentName")
  HealthRecordDto toDto(HealthRecord healthRecord);

  List<HealthRecordDto> toDtoList(List<HealthRecord> healthRecords);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "animal", ignore = true)
  @Mapping(target = "treatment", ignore = true)
  @Mapping(target = "status", ignore = true)
  HealthRecord toEntity(HealthRecordDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "animal", ignore = true)
  @Mapping(target = "treatment", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateFromDto(HealthRecordDto dto, @MappingTarget HealthRecord healthRecord);
}

