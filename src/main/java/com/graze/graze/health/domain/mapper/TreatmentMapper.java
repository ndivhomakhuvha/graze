package com.graze.graze.health.domain.mapper;

import com.graze.graze.health.domain.Treatment;
import com.graze.graze.health.domain.dto.TreatmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TreatmentMapper {

  TreatmentDto toDto(Treatment treatment);

  List<TreatmentDto> toDtoList(List<Treatment> treatments);

  @Mapping(target = "id", ignore = true)
  Treatment toEntity(TreatmentDto dto);

  @Mapping(target = "id", ignore = true)
  void updateFromDto(TreatmentDto dto, @MappingTarget Treatment treatment);
}

