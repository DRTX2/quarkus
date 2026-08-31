package com.drtx.qks.application.services.chaos;

import com.drtx.qks.domain.model.*;
import com.drtx.qks.domain.ports.out.chaos.ChaosEnginePort;
import com.drtx.qks.domain.ports.out.chaos.ChaosExperimentRepositoryPort;
import com.drtx.qks.domain.ports.out.chaos.ResilienceAnalysisPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChaosApplicationServiceTest {

    private ChaosEnginePort chaosEngine;
    private ResilienceAnalysisPort aiAnalyzer;
    private ChaosExperimentRepositoryPort repository;
    private MeterRegistry registry;
    private ChaosApplicationService service;

    @BeforeEach
    void setUp() {
        chaosEngine = Mockito.mock(ChaosEnginePort.class);
        aiAnalyzer = Mockito.mock(ResilienceAnalysisPort.class);
        repository = Mockito.mock(ChaosExperimentRepositoryPort.class);
        registry = new SimpleMeterRegistry(); // Usamos un registry simple real para tests

        // Make repository return whatever is passed to save
        when(repository.save(any(ChaosExperiment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service = new ChaosApplicationService(chaosEngine, aiAnalyzer, repository, registry);
    }

    @Test
    @DisplayName("Should execute chaos experiment, analyze resilience with AI, and persist result")
    void shouldExecuteExperimentSuccessfully() {
        // Arrange
        ChaosExecutionResult mockResult = new ChaosExecutionResult(true, "Pod nginx-123 killed", 1850L);
        when(chaosEngine.injectChaos(any(ChaosExperiment.class))).thenReturn(mockResult);

        ResilienceEvaluation mockEval = new ResilienceEvaluation(
                92,
                true,
                1850L,
                "LOW",
                "# Resilience Report\nScore: 92",
                List.of("Scale replicas")
        );
        when(aiAnalyzer.evaluateResilience(any(ChaosExperiment.class), eq("Pod nginx-123 killed"), eq(1850L))).thenReturn(mockEval);

        // Act
        ChaosExperiment experiment = service.executeExperiment("production", "order-service", ChaosType.POD_TERMINATION, "sre_david");

        // Assert
        assertNotNull(experiment);
        assertNotNull(experiment.getId());
        assertEquals("production", experiment.getTargetNamespace());
        assertEquals("order-service", experiment.getTargetDeployment());
        assertEquals(ChaosType.POD_TERMINATION, experiment.getType());
        assertEquals(ChaosStatus.COMPLETED, experiment.getStatus());
        assertEquals(92, experiment.getResilienceScore());
        assertEquals(1850L, experiment.getRecoveryTimeMs());
        assertTrue(experiment.getRecoveredSuccessfully());
        assertEquals("sre_david", experiment.getExecutedBy());

        // Verify interactions
        verify(chaosEngine, times(1)).injectChaos(any(ChaosExperiment.class));
        verify(aiAnalyzer, times(1)).evaluateResilience(any(ChaosExperiment.class), eq("Pod nginx-123 killed"), eq(1850L));
        verify(repository, atLeast(2)).save(any(ChaosExperiment.class));
    }

    @Test
    @DisplayName("Should retrieve experiment by ID from repository")
    void shouldGetExperimentById() {
        // Arrange
        ChaosExperiment mockExp = new ChaosExperiment("exp-uuid-123", "default", "payment-service", ChaosType.NETWORK_LATENCY, "admin");
        when(repository.findById("exp-uuid-123")).thenReturn(Optional.of(mockExp));

        // Act
        Optional<ChaosExperiment> result = service.getExperimentById("exp-uuid-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("payment-service", result.get().getTargetDeployment());
        verify(repository, times(1)).findById("exp-uuid-123");
    }
}
