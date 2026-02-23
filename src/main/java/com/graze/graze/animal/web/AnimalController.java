package com.graze.graze.animal.web;

import com.graze.graze.animal.application.AnimalService;
import com.graze.graze.animal.domain.dto.AnimalDto;
import com.graze.graze.animal.domain.enums.AnimalType;
import com.graze.graze.animal.domain.enums.Gender;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animals")
public class AnimalController {
  private final AnimalService service;

  public AnimalController(AnimalService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('manage-animal')")
  public AnimalDto register(@RequestBody AnimalDto animalDto) {
    return service.register(animalDto);
  }

  @GetMapping
  @PreAuthorize("hasRole('view-animal')")
  public List<AnimalDto> findAll(
    @RequestParam(required = false) AnimalType type,
    @RequestParam(required = false) Gender gender
  ) {
    if (type != null && gender != null) {
      return service.findByTypeAndGender(type, gender);
    } else if (type != null) {
      return service.findByType(type);
    } else if (gender != null) {
      return service.findByGender(gender);
    }
    return service.findAll();
  }

  @GetMapping("/{tagNo}")
  @PreAuthorize("hasRole('view-animal')")
  public AnimalDto findByTagNo(@PathVariable String tagNo) {
    return service.findByTagNo(tagNo);
  }

  @GetMapping("/{tagNo}/offspring")
  @PreAuthorize("hasRole('view-animal')")
  public List<AnimalDto> findOffspring(@PathVariable String tagNo) {
    return service.findOffspring(tagNo);
  }

  @PutMapping("/{tagNo}")
  @PreAuthorize("hasRole('manage-animal')")
  public AnimalDto update(@PathVariable String tagNo, @RequestBody AnimalDto animalDto) {
    return service.update(tagNo, animalDto);
  }

  @DeleteMapping("/{tagNo}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('manage-animal')")
  public void delete(@PathVariable String tagNo) {
    service.delete(tagNo);
  }
}
