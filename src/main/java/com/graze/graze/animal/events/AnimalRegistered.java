
package com.graze.graze.animal.events;

import com.graze.graze.animal.domain.Animal;
import org.springframework.modulith.NamedInterface;
@NamedInterface("AnimalRegistered")
public record AnimalRegistered(Animal animal) {
}
