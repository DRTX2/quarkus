package com.drtx.qks.domain.model;

public enum UserRole {
    USER,
    ADMIN,
    MODERATOR;

    public String getAuthority() {
        return "ROLE_"+this.name();
    }
}
