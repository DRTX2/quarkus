package com.drtx.qks;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

/**
 * Resource de ejemplo mostrando cómo acceder a la configuración
 */
@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Configuration", description = "Configuration information")
public class ConfigResource {

    @Inject
    AppConfig appConfig;

    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @ConfigProperty(name = "quarkus.application.version")
    String applicationVersion;

    @ConfigProperty(name = "quarkus.http.port")
    int serverPort;

    // Ejemplo de valor opcional con default
    @ConfigProperty(name = "app.custom-property", defaultValue = "default-value")
    String customProperty;

    @GET
    @Path("/info")
    @Operation(summary = "Get application configuration info",
               description = "Returns non-sensitive configuration information")
    public Map<String, Object> getConfigInfo() {
        return Map.of(
            "application", Map.of(
                "name", applicationName,
                "version", applicationVersion,
                "environment", appConfig.environment()
            ),
            "server", Map.of(
                "port", serverPort
            ),
            "features", Map.of(
                "registration", appConfig.features().registrationEnabled(),
                "emailVerification", appConfig.features().emailVerificationRequired(),
                "adminApi", appConfig.features().adminApiEnabled()
            )
        );
    }

    @GET
    @Path("/health-simple")
    @Operation(summary = "Simple health check",
               description = "Returns a simple health status")
    public Map<String, String> healthCheck() {
        return Map.of(
            "status", "UP",
            "environment", appConfig.environment(),
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }
}

