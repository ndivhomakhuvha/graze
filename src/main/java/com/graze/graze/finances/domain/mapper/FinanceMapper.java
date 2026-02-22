package com.graze.graze.finances.domain.mapper;

import com.graze.graze.finances.domain.Finance;
import com.graze.graze.finances.domain.dto.FinanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FinanceMapper {

  FinanceDto toDto(Finance finance);

  List<FinanceDto> toDtoList(List<Finance> finances);

  @Mapping(target = "id", ignore = true)
  Finance toEntity(FinanceDto dto);

  @Mapping(target = "id", ignore = true)
  void updateFromDto(FinanceDto dto, @MappingTarget Finance finance);
}

