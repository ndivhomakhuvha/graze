
package com.graze.graze.animal.events;

import org.springframework.modulith.NamedInterface;
@NamedInterface("AnimalRegistered")
public record AnimalRegistered(String tagNo,
                               String name,
                               String type) {
}
