package com.graze.graze.animal.domain.mapper;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.domain.dto.AnimalDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

  @Mapping(target = "tagNo", ignore = true)
  @Mapping(target = "mother", ignore = true)
  @Mapping(target = "father", ignore = true)
  @Mapping(target = "childrenFromMother", ignore = true)
  @Mapping(target = "childrenFromFather", ignore = true)
  @Mapping(target = "healthRecords", ignore = true)
  Animal toAnimal(AnimalDto dto);

  @Mapping(source = "mother.tagNo", target = "motherTagNo")
  @Mapping(source = "father.tagNo", target = "fatherTagNo")
  AnimalDto toDto(Animal animal);

  List<AnimalDto> toDtoList(List<Animal> animals);

  @Mapping(target = "tagNo", ignore = true)
  @Mapping(target = "mother", ignore = true)
  @Mapping(target = "father", ignore = true)
  @Mapping(target = "childrenFromMother", ignore = true)
  @Mapping(target = "childrenFromFather", ignore = true)
  @Mapping(target = "healthRecords", ignore = true)
  void updateAnimalFromDto(AnimalDto dto, @MappingTarget Animal animal);
}
