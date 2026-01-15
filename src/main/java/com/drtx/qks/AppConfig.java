package com.drtx.qks;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * Configuración de la aplicación mapeada desde variables de entorno y application.yml
 *
 * Ejemplo de uso:
 * <pre>
 * {@code
 * @Inject
 * AppConfig config;
 *
 * String env = config.environment();
 * }
 * </pre>
 */
@ConfigMapping(prefix = "app")
public interface AppConfig {

    /**
     * Entorno de la aplicación (development, test, production)
     * Variable de entorno: APP_ENV
     */
    @WithName("environment")
    @WithDefault("development")
    String environment();

    /**
     * Nombre de la aplicación
     * Variable de entorno: APP_NAME
     */
    @WithName("name")
    @WithDefault("Learning Quarkus")
    String name();

    /**
     * Características de la aplicación
     */
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

