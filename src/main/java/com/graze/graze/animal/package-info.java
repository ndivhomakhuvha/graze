@ApplicationModule(
  allowedDependencies = {
    "common :: exceptions",
    "user :: domain",
    "user :: repository",
    "user :: application"
  }
)
package com.graze.graze.animal;

import org.springframework.modulith.ApplicationModule;
