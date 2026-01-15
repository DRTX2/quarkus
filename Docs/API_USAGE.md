# API REST con Quarkus - Guía de Uso

## 🚀 Tu API REST está lista!

### Endpoints Disponibles

#### 1. **Greeting API** (`/api/greeting`)

##### GET `/api/greeting`
Obtiene un mensaje de saludo simple
```bash
curl http://localhost:8080/api/greeting
```

##### GET `/api/greeting/{name}`
Obtiene un mensaje de saludo personalizado
```bash
curl http://localhost:8080/api/greeting/David
```

##### POST `/api/greeting`
Crea un saludo personalizado
```bash
curl -X POST http://localhost:8080/api/greeting \
  -H "Content-Type: application/json" \
  -d '{"name": "David", "greeting": "Hola"}'
```

#### 2. **Entity CRUD API** (`/api/entities`)

##### GET `/api/entities`
Lista todas las entidades
```bash
curl http://localhost:8080/api/entities
```

##### GET `/api/entities/{id}`
Obtiene una entidad por ID
```bash
curl http://localhost:8080/api/entities/1
```

##### POST `/api/entities`
Crea una nueva entidad
```bash
curl -X POST http://localhost:8080/api/entities \
  -H "Content-Type: application/json" \
  -d '{"field": "Nuevo valor"}'
```

##### PUT `/api/entities/{id}`
Actualiza una entidad existente
```bash
curl -X PUT http://localhost:8080/api/entities/1 \
  -H "Content-Type: application/json" \
  -d '{"field": "Valor actualizado"}'
```

##### DELETE `/api/entities/{id}`
Elimina una entidad
```bash
curl -X DELETE http://localhost:8080/api/entities/1
```

### 📊 Endpoints de Monitoreo

- **Swagger UI**: http://localhost:8080/swagger-ui
- **OpenAPI Spec**: http://localhost:8080/q/openapi
- **Health Check**: http://localhost:8080/health
- **Liveness**: http://localhost:8080/health/live
- **Readiness**: http://localhost:8080/health/ready
- **Metrics (Prometheus)**: http://localhost:8080/metrics

### 🏃 Cómo ejecutar

#### Opción 1: Modo desarrollo (con hot reload)
```bash
./gradlew quarkusDev
```

#### Opción 2: Build y ejecución
```bash
./gradlew build
java -jar build/quarkus-app/quarkus-run.jar
```

### 🐘 Base de Datos PostgreSQL

Asegúrate de tener PostgreSQL ejecutándose. Puedes usar Docker:

```bash
docker run --name postgres-quarkus \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=quarkus_db \
  -p 5432:5432 \
  -d postgres:16
```

O configura las variables de entorno:
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=quarkus_db
export DB_USER=postgres
export DB_PASSWORD=postgres
```

### 🧪 Testing

Ejecuta los tests:
```bash
./gradlew test
```

### 📝 Notas

- La aplicación usa **Flyway** para migraciones de base de datos
- Los logs están en formato legible, cambia `quarkus.log.console.json` a `true` para JSON
- CORS está habilitado para `http://localhost:3000` y `http://localhost:4200`
- La validación está activa (campos requeridos se validan automáticamente)

### 🎯 Próximos pasos para una app más compleja

1. **Seguridad**: Implementar JWT/OIDC authentication
2. **Testing**: Agregar tests unitarios e integración
3. **Servicios**: Crear capa de servicios entre recursos y repositorios
4. **DTOs**: Usar MapStruct para mapear entidades a DTOs
5. **Paginación**: Implementar paginación en los endpoints de listado
6. **Filtros**: Agregar filtros y búsqueda
7. **Eventos**: Usar Kafka para mensajería asíncrona
8. **Cache**: Implementar cache en consultas frecuentes

¡Tu API REST está completamente funcional! 🎉

