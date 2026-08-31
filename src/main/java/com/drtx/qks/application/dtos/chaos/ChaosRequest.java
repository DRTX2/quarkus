package com.drtx.qks.application.dtos.chaos;

import com.drtx.qks.domain.model.ChaosType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChaosRequest {

    @NotBlank(message = "Target namespace is required")
    private String targetNamespace;

    @NotBlank(message = "Target deployment is required")
    private String targetDeployment;

    @NotNull(message = "Chaos type is required")
    private ChaosType type;

    public String getTargetNamespace() { return targetNamespace; }
    public void setTargetNamespace(String targetNamespace) { this.targetNamespace = targetNamespace; }

    public String getTargetDeployment() { return targetDeployment; }
    public void setTargetDeployment(String targetDeployment) { this.targetDeployment = targetDeployment; }

    public ChaosType getType() { return type; }
    public void setType(ChaosType type) { this.type = type; }
}
