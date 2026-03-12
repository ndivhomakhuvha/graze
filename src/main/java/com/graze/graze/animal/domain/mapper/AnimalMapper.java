package com.graze.graze.animal.domain.mapper;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.domain.AnimalOwners;
import com.graze.graze.animal.domain.dto.AnimalDto;
import com.graze.graze.animal.domain.dto.AnimalOwnerDto;
import com.graze.graze.animal.domain.dto.CreateAndEditAnimalDTO;
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
  @Mapping(target = "owners", ignore = true)
  Animal toAnimal(CreateAndEditAnimalDTO dto);

  @Mapping(source = "mother.tagNo", target = "motherTagNo")
  @Mapping(source = "father.tagNo", target = "fatherTagNo")
  AnimalDto toDto(Animal animal);

  List<AnimalDto> toDtoList(List<Animal> animals);


  @Mapping(target = "tagNo", ignore = true)
  @Mapping(target = "mother", ignore = true)
  @Mapping(target = "father", ignore = true)
  @Mapping(target = "childrenFromMother", ignore = true)
  @Mapping(target = "childrenFromFather", ignore = true)
  @Mapping(target = "owners", ignore = true)
  void updateAnimalFromDto(CreateAndEditAnimalDTO dto, @MappingTarget Animal animal);

  // ── Owner mapping ──

  AnimalOwnerDto toOwnerDto(AnimalOwners owner);

  List<AnimalOwnerDto> toOwnerDtoList(List<AnimalOwners> owners);
}
