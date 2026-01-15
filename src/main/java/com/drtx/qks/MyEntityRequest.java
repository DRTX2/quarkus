package com.drtx.qks;

import jakarta.validation.constraints.NotBlank;

public record MyEntityRequest(
    @NotBlank(message = "Field is required")
    String field
) {}

