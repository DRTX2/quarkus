package com.drtx.qks.application.dtos.auth;

import java.util.Set;

public record UserInfo(
                java.util.UUID id,
                String username,
                String email,
                Set<String> roles,
                boolean enabled) {
}
