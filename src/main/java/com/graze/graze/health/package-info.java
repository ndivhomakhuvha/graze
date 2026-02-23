@ApplicationModule(
  allowedDependencies = {
    "animal :: domain",
    "animal :: repository",
    "animal :: events",
    "common :: exceptions"
  }
)
package com.graze.graze.health;

import org.springframework.modulith.ApplicationModule;
