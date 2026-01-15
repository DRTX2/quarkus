package com.drtx.qks;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyEntityRepository implements PanacheRepository<MyEntity> {
    // Métodos personalizados si los necesitas, por ejemplo:
    // public List<MyEntity> findByField(String field) {
    //     return list("field", field);
    // }
}

