package com.drtx.qks.adapters.in.health;

import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class ChaosEngineHealthCheck implements HealthCheck {

    @Inject
    KubernetesClient kubernetesClient;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("KubeChaos Engine");

        try {
            // Verificamos si podemos leer la versión del clúster
            String version = kubernetesClient.getKubernetesVersion().getGitVersion();
            
            responseBuilder.up()
                    .withData("kubernetes_version", version)
                    .withData("mode", "LIVE_CLUSTER");
                    
        } catch (Exception e) {
            // Fallback: Si no hay clúster, indicamos que estamos en modo simulación, pero el health es UP
            responseBuilder.up()
                    .withData("mode", "DEV_SIMULATION")
                    .withData("warning", "No live Kubernetes cluster found. Engine is operating in simulation mode.");
        }

        return responseBuilder.build();
    }
}
