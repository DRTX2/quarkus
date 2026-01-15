package com.drtx.qks;

import jakarta.validation.constraints.NotBlank;

public record GreetingRequest(
    @NotBlank(message = "Name is required")
    String name,
    String greeting
) {}

