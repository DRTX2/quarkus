package com.drtx.qks.application.services.chaos;

import com.drtx.qks.domain.model.*;
import com.drtx.qks.domain.ports.in.chaos.ExecuteChaosUseCase;
import com.drtx.qks.domain.ports.in.chaos.GetChaosExperimentUseCase;
import com.drtx.qks.domain.ports.in.chaos.ListChaosExperimentsUseCase;
import com.drtx.qks.domain.ports.out.chaos.ChaosEnginePort;
import com.drtx.qks.domain.ports.out.chaos.ChaosExperimentRepositoryPort;
import com.drtx.qks.domain.ports.out.chaos.ResilienceAnalysisPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ChaosApplicationService implements ExecuteChaosUseCase, GetChaosExperimentUseCase, ListChaosExperimentsUseCase {

    private static final Logger LOG = Logger.getLogger(ChaosApplicationService.class);

    private final ChaosEnginePort chaosEngine;
    private final ResilienceAnalysisPort aiAnalyzer;
    private final ChaosExperimentRepositoryPort repository;
    private final MeterRegistry registry;

    @Inject
    public ChaosApplicationService(ChaosEnginePort chaosEngine,
                                   ResilienceAnalysisPort aiAnalyzer,
                                   ChaosExperimentRepositoryPort repository,
                                   MeterRegistry registry) {
        this.chaosEngine = chaosEngine;
        this.aiAnalyzer = aiAnalyzer;
        this.repository = repository;
        this.registry = registry;
    }

    @Override
    @Transactional
    public ChaosExperiment executeExperiment(String namespace, String deployment, ChaosType type, String username) {
        String id = UUID.randomUUID().toString();
        ChaosExperiment experiment = new ChaosExperiment(id, namespace, deployment, type, username);
        experiment.setStatus(ChaosStatus.RUNNING);

        LOG.infof("Starting Chaos Experiment [%s] for %s/%s by %s", id, namespace, deployment, username);
        repository.save(experiment);

        try {
            // 1. Inyectar Caos en la infraestructura / simulador K8s
            ChaosExecutionResult result = chaosEngine.injectChaos(experiment);
            experiment.setExecutionLogs(result.getLogs());
            experiment.setRecoveryTimeMs(result.getRecoveryTimeMs());
            experiment.setRecoveredSuccessfully(result.isSuccess());

            // 2. Evaluar Resiliencia con Inteligencia Artificial (AIOps - ISO 25010)
            ResilienceEvaluation eval = aiAnalyzer.evaluateResilience(experiment, result.getLogs(), result.getRecoveryTimeMs());
            experiment.setResilienceScore(eval.getScore());
            experiment.setResilienceReport(eval.getReportMarkdown());
            experiment.setStatus(ChaosStatus.COMPLETED);
            experiment.setCompletedAt(LocalDateTime.now());

            // 3. Registrar Métricas de SLI en Prometheus
            Tags tags = Tags.of("namespace", namespace, "deployment", deployment, "chaos_type", type.name());
            registry.counter("chaos_experiments_total", tags).increment();
            registry.gauge("chaos_resilience_score", tags, eval.getScore());
            
            if (result.getRecoveryTimeMs() != null) {
                registry.timer("chaos_recovery_time_ms", tags).record(java.time.Duration.ofMillis(result.getRecoveryTimeMs()));
            }

            LOG.infof("Chaos Experiment [%s] completed with Resilience Score: %d/100", id, eval.getScore());
        } catch (Exception e) {
            LOG.errorf("Chaos Experiment [%s] failed: %s", id, e.getMessage());
            experiment.setStatus(ChaosStatus.FAILED);
            experiment.setExecutionLogs("Execution failed with exception: " + e.getMessage());
            experiment.setCompletedAt(LocalDateTime.now());
            
            Tags tags = Tags.of("namespace", namespace, "deployment", deployment, "chaos_type", type.name());
            registry.counter("chaos_experiments_failed_total", tags).increment();
        }

        return repository.save(experiment);
    }

    @Override
    public Optional<ChaosExperiment> getExperimentById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<ChaosExperiment> listRecentExperiments(int limit) {
        int safeLimit = (limit <= 0 || limit > 100) ? 20 : limit;
        return repository.findAll(safeLimit);
    }

    @Override
    public List<ChaosExperiment> listExperimentsByNamespace(String namespace) {
        return repository.findByNamespace(namespace);
    }
}
