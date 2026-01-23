package com.drtx.qks.domain.constants;

public enum RoleEnum {
    USER("USER"),
    ADMIN("ADMIN");

    private final String roleName;

    RoleEnum(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
