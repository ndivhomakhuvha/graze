package com.graze.graze;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {
    @Test
    void verifyModularity() {
        ApplicationModules.of(GrazeApplication.class).verify();
    }
}
