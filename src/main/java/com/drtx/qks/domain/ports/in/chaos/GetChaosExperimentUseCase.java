package com.drtx.qks.domain.ports.in.chaos;

import com.drtx.qks.domain.model.ChaosExperiment;
import java.util.Optional;

public interface GetChaosExperimentUseCase {
    Optional<ChaosExperiment> getExperimentById(String id);
}
