package com.graze.graze.animal.web;

import com.graze.graze.animal.application.AnimalService;
import com.graze.graze.animal.domain.Animal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animals")
public class AnimalController {
  private final AnimalService service;

  public AnimalController(AnimalService service) {
    this.service = service;
  }

  @PostMapping
  public void register(
    @RequestBody Animal animal
  ) {
    service.register(animal);
  }
}
