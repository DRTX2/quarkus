package com.drtx.qks.domain.ports.in.chaos;

import com.drtx.qks.domain.model.ChaosExperiment;
import com.drtx.qks.domain.model.ChaosType;

public interface ExecuteChaosUseCase {
    ChaosExperiment executeExperiment(String namespace, String deployment, ChaosType type, String username);
}
