package com.drtx.qks.infrastructure.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "app")
public interface AppConfig {

    @WithName("environment")
    @WithDefault("development")
    Environment environment();

    enum Environment {
        development,
        staging,
        production
    }

    @WithName("name")
    @WithDefault("Learning Quarkus")
    String name();

    Features features();

    interface Features {
        @WithDefault("true")
        boolean registrationEnabled();

        @WithDefault("true")
        boolean emailVerificationRequired();

        @WithDefault("true")
        boolean adminApiEnabled();
    }
}

