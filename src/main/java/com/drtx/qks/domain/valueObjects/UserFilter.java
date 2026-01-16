package com.drtx.qks.domain.valueObjects;

public record UserFilter (
    String username,
    String email,
    String role,
    Boolean enabled
){

}
