package com.drtx.qks.domain.valueObjects;

import java.util.List;

public record Page<T> (
    List<T> content,
    int number,
    int size,
    long totalElements,
    int totalPages){
}
