package com.drtx.qks.adapters.in.scheduler;

import com.drtx.qks.domain.model.ChaosType;
import com.drtx.qks.domain.ports.in.chaos.ExecuteChaosUseCase;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Random;

@ApplicationScoped
public class ChaosMonkeyScheduler {

    private static final Logger LOG = Logger.getLogger(ChaosMonkeyScheduler.class);
    private final Random random = new Random();

    @Inject
    ExecuteChaosUseCase executeChaosUseCase;

    @ConfigProperty(name = "chaos.monkey.enabled", defaultValue = "false")
    boolean isEnabled;

    @ConfigProperty(name = "chaos.monkey.target-namespace", defaultValue = "production")
    String namespace;

    @ConfigProperty(name = "chaos.monkey.target-deployment", defaultValue = "order-service")
    String deployment;

    @Scheduled(cron = "{chaos.monkey.cron}")
    void runChaosMonkey() {
        if (!isEnabled) {
            LOG.debug("Chaos Monkey is disabled. Skipping scheduled execution.");
            return;
        }

        LOG.infof("🐒 Chaos Monkey awakened! Targeting %s/%s", namespace, deployment);

        // Randomly pick a chaos type for unpredictability
        ChaosType[] types = ChaosType.values();
        ChaosType selectedType = types[random.nextInt(types.length)];

        try {
            executeChaosUseCase.executeExperiment(
                    namespace,
                    deployment,
                    selectedType,
                    "system:chaos-monkey"
            );
        } catch (Exception e) {
            LOG.errorf("Monkey encountered an unexpected error: %s", e.getMessage());
        }
    }
}
