package com.drtx.qks.domain.ports.in.chaos;

import com.drtx.qks.domain.model.ChaosExperiment;
import java.util.List;

public interface ListChaosExperimentsUseCase {
    List<ChaosExperiment> listRecentExperiments(int limit);
    List<ChaosExperiment> listExperimentsByNamespace(String namespace);
}
