# Learning Quarkus - API REST Completa

Este proyecto es una aplicación completa construida con Quarkus, el Supersonic Subatomic Java Framework.

## 🚀 Inicio Rápido

### Opción 1: Script de inicio automático
```bash
./start.sh                    # Usa .env.development por defecto
./start.sh development        # Especificar entorno
./start.sh test              # Entorno de testing
```

### Opción 2: Con scripts específicos
```bash
# Cargar configuración y ejecutar
./scripts/run-dev.sh          # Desarrollo
./scripts/load-env.sh test    # Test
```

### Opción 3: Manual
1. Configura el entorno:
```bash
cp .env.development .env      # O edita .env directamente
```

2. Inicia PostgreSQL:
```bash
docker-compose up -d
```

3. Ejecuta la aplicación:
```bash
./gradlew quarkusDev
```

## ⚙️ Configuración y Variables de Entorno

El proyecto usa archivos `.env` para gestionar la configuración:

```bash
.env                  # Archivo actual (no subir a git)
.env.example         # Template con todas las variables
.env.development     # Preconfigurado para desarrollo
.env.test           # Preconfigurado para tests
.env.production     # Template para producción
```

### Variables Principales

```bash
# Servidor
SERVER_PORT=8080
SERVER_HOST=0.0.0.0

# Base de Datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=quarkus_db
DB_USER=postgres
DB_PASSWORD=postgres

# Logging
LOG_LEVEL=DEBUG
```

Ver **[CONFIGURACION_SECRETOS.md](CONFIGURACION_SECRETOS.md)** para la guía completa.

## 📚 Documentación

### 🎯 Empezar Aquí
- **[RESUMEN_FINAL_CONFIGURACION.md](RESUMEN_FINAL_CONFIGURACION.md)** - ⭐ **LEE ESTO PRIMERO** - Resumen completo de todo
- **[README.md](README.md)** - Este archivo (documentación general)

### 🔧 Configuración y Secretos
- **[RESUMEN_CONFIGURACION.md](RESUMEN_CONFIGURACION.md)** - Resumen rápido de configuración
- **[CONFIGURACION_SECRETOS.md](CONFIGURACION_SECRETOS.md)** - Guía completa de configuración y seguridad
- **[EJEMPLOS_ENV.md](EJEMPLOS_ENV.md)** - Ejemplos prácticos de uso de variables de entorno

### 🚀 API y Arquitectura
- **[RESUMEN_CAMBIOS.md](RESUMEN_CAMBIOS.md)** - Resumen de la arquitectura de la aplicación
- **[API_USAGE.md](API_USAGE.md)** - Guía de uso de la API REST con ejemplos curl

### 🌐 Documentación Interactiva
- **Swagger UI**: http://localhost:8080/swagger-ui (cuando la app esté corriendo)
- **Dev UI**: http://localhost:8080/q/dev (solo en modo desarrollo)

## 🔌 Endpoints Principales

### API REST
- `GET /api/greeting` - Mensaje de saludo
- `GET /api/greeting/{name}` - Saludo personalizado
- `POST /api/greeting` - Crear saludo custom
- `GET /api/entities` - Listar entidades (CRUD completo)

### Observabilidad
- `/health` - Health checks
- `/metrics` - Métricas Prometheus
- `/swagger-ui` - Documentación interactiva

## 🛠 Tecnologías

- **Quarkus 3.x** - Framework
- **PostgreSQL** - Base de datos
- **Flyway** - Migraciones
- **Hibernate ORM + Panache** - Persistencia
- **RESTEasy Reactive** - REST API
- **OpenAPI/Swagger** - Documentación
- **Micrometer + Prometheus** - Métricas

Si quieres aprender más sobre Quarkus, visita: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/learning-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and Jakarta Persistence
- RESTeasy Problem ([guide](https://github.com/quarkiverse/quarkus-resteasy-problem/blob/main/README.md)): Problem Details for HTTP APIs (RFC-7807) implementation for Quarkus / RESTeasy.
- RESTEasy Classic's REST Client Mutiny support ([guide](https://quarkus.io/guides/resteasy-client)): Enable Mutiny for the REST client
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, Jakarta Persistence)
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- YAML Configuration ([guide](https://quarkus.io/guides/config-yaml)): Use YAML to configure your Quarkus application
- SmallRye JWT ([guide](https://quarkus.io/guides/security-jwt)): Secure your applications with JSON Web Token
- Camel MapStruct ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/mapstruct.html)): Type Conversion using Mapstruct

## Provided Code

### YAML Config

Configure your application with YAML

[Related guide section...](https://quarkus.io/guides/config-reference#configuration-examples)

The Quarkus application configuration is located in `src/main/resources/application.yml`.

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)


