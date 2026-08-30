# OpenAPI y Scalar

La API publica un contrato OpenAPI generado por SmallRye OpenAPI, la integración compatible con Quarkus. `springdoc-openapi` pertenece al ecosistema Spring Boot y no debe agregarse a este proyecto.

## URLs

Con la aplicación ejecutándose en `http://localhost:8080`:

| Recurso | URL |
| --- | --- |
| Scalar | <http://localhost:8080/scalar/> |
| Swagger UI | <http://localhost:8080/swagger-ui/> |
| OpenAPI YAML | <http://localhost:8080/q/openapi> |
| OpenAPI JSON | <http://localhost:8080/q/openapi?format=json> |

Scalar carga su interfaz desde jsDelivr y utiliza `/q/openapi` como contrato. Por ello, el navegador necesita acceso a Internet para descargar la interfaz; el contrato OpenAPI continúa disponible localmente aunque el CDN no esté accesible.

## Inicio

```bash
docker compose up -d
./gradlew quarkusDev
```

Abre Scalar y prueba primero `POST /api/auth/register` o `POST /api/auth/login`. Copia el valor `accessToken` de la respuesta, pulsa **Authentication** e introdúcelo en el esquema `bearer-jwt`. Scalar agregará el encabezado siguiente a los endpoints protegidos:

```http
Authorization: Bearer <access-token>
```

## Endpoints documentados

### Autenticación

| Método | Ruta | Acceso |
| --- | --- | --- |
| POST | `/api/auth/register` | Público |
| POST | `/api/auth/login` | Público |
| POST | `/api/auth/refresh` | Público |
| POST | `/api/auth/logout` | USER o ADMIN |
| GET | `/api/auth/me` | USER o ADMIN |
| PUT | `/api/auth/change-password` | USER o ADMIN |
| GET | `/api/auth/admin` | ADMIN |

### Usuarios

Todos los endpoints de `/api/users` requieren el rol `ADMIN`.

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/users` | Lista y filtra usuarios con paginación |
| GET | `/api/users/{uuid}` | Obtiene un usuario |
| PUT | `/api/users/{uuid}` | Actualiza un usuario |
| DELETE | `/api/users/{uuid}` | Elimina un usuario |

## Configuración

La extensión `io.quarkus:quarkus-smallrye-openapi` genera el contrato a partir de los recursos Jakarta REST y las anotaciones MicroProfile OpenAPI. Los metadatos generales y el esquema JWT están en:

- `src/main/java/com/drtx/qks/infrastructure/config/OpenApiConfig.java`
- `src/main/resources/application.yml`

La interfaz de Scalar es una página estática ubicada en:

- `src/main/resources/META-INF/resources/scalar/index.html`

Para cambiar la ruta del contrato, configura `quarkus.smallrye-openapi.path` y actualiza `data-url` en la página de Scalar.
