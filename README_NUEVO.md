# 🚀 Learning Quarkus - API REST Completa

Aplicación completa con Quarkus 3.30.6, PostgreSQL, JWT, Flyway y más.

## 📋 Características

✅ **REST API** con RESTEasy Reactive  
✅ **Base de datos** PostgreSQL con Hibernate ORM Panache  
✅ **Migraciones** con Flyway  
✅ **Seguridad** JWT con SmallRye JWT  
✅ **Validación** con Hibernate Validator  
✅ **Documentación** con OpenAPI/Swagger UI  
✅ **Health Checks** y Métricas (Prometheus)  
✅ **Logging** estructurado con JSON  
✅ **Perfiles** (dev/prod) configurables desde .env  
✅ **Testing** con JUnit 5 y RestAssured  

## 🏗️ Estructura del Proyecto

```
learning-quarkus/
├── .env                              # Variables locales (gitignored)
├── .env.dev                          # Template desarrollo
├── .env.prod                         # Template producción
├── build.gradle                      # Configuración Gradle
├── src/
│   ├── main/
│   │   ├── java/com/drtx/qks/
│   │   │   ├── security/             # 🔐 Autenticación JWT
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── AuthResource.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── LoginResponse.java
│   │   │   ├── MyEntity.java
│   │   │   ├── MyEntityRepository.java
│   │   │   └── MyEntityResource.java
│   │   └── resources/
│   │       ├── application.yml       # Config común
│   │       ├── application-dev.yml   # Config desarrollo
│   │       ├── application-prod.yml  # Config producción
│   │       ├── publicKey.pem         # Clave pública JWT
│   │       ├── privateKey.pem        # Clave privada JWT
│   │       └── db/migration/
│   │           └── V1__Initial_schema.sql
│   └── test/
└── docs/
    ├── CONFIGURACION_PERFILES.md     # Guía de perfiles
    ├── GUIA_JWT.md                   # Guía de JWT
    └── API_USAGE.md                  # Ejemplos de uso
```

## ⚙️ Requisitos Previos

- **Java 21+**
- **Gradle 8+** (incluido via wrapper)
- **PostgreSQL 14+**
- **Docker** (opcional, para PostgreSQL)

## 🚀 Inicio Rápido

### 1. Clonar y Configurar

```bash
# Copiar template de desarrollo
cp .env.dev .env

# Editar si necesitas cambiar valores (BD, puertos, etc.)
nano .env
```

### 2. Iniciar Base de Datos

```bash
# Opción 1: Docker Compose
docker-compose up -d

# Opción 2: PostgreSQL local
# Asegúrate de que PostgreSQL esté corriendo
# y actualiza las credenciales en .env
```

### 3. Ejecutar Aplicación

```bash
# Modo desarrollo (hot-reload)
./gradlew quarkusDev

# La aplicación estará disponible en:
# http://localhost:8080
```

## 📚 Endpoints Disponibles

### 🔐 Autenticación

```bash
# Login (obtener token JWT)
POST /api/auth/login
{
  "username": "admin",
  "password": "admin"
}

# Obtener info del usuario actual
GET /api/auth/me
Headers: Authorization: Bearer <token>

# Endpoint solo para admin
GET /api/auth/admin
Headers: Authorization: Bearer <token>
```

**Usuarios de prueba:**
- `admin/admin` → roles: ["admin", "user"]
- `user/user` → roles: ["user"]

### 📊 API REST

```bash
# Listar entidades
GET /api/myentities

# Obtener por ID
GET /api/myentities/{id}

# Crear
POST /api/myentities
{
  "field": "valor"
}

# Actualizar
PUT /api/myentities/{id}
{
  "field": "nuevo valor"
}

# Eliminar
DELETE /api/myentities/{id}
```

### 🔍 Observabilidad

```bash
# Swagger UI
http://localhost:8080/swagger-ui

# Health Check
http://localhost:8080/health

# Métricas Prometheus
http://localhost:8080/metrics

# Configuración
http://localhost:8080/api/config/info
```

## 🎯 Perfiles de Configuración

El proyecto usa **2 perfiles** controlados por la variable `QUARKUS_PROFILE` en el `.env`:

### Desarrollo (`dev`)
```bash
QUARKUS_PROFILE=dev
```
- SQL logging activado
- Swagger UI visible
- Logs en nivel DEBUG
- Email en modo mock
- Base de datos: `quarkus_dev`

### Producción (`prod`)
```bash
QUARKUS_PROFILE=prod
```
- SQL logging desactivado
- Swagger UI oculto
- Logs en JSON (nivel INFO)
- Email real con SMTP
- Pool de conexiones optimizado

**Ver documentación completa:** [CONFIGURACION_PERFILES.md](Docs/CONFIGURACION_PERFILES.md)

## 🔐 Seguridad JWT

### Configuración

Las claves RSA ya están generadas en `src/main/resources/`:
- `publicKey.pem` - Validar tokens
- `privateKey.pem` - Firmar tokens

### Uso Básico

```bash
# 1. Obtener token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' \
  | jq -r '.token')

# 2. Usar token en requests
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

### Proteger Endpoints

```java
@GET
@RolesAllowed({"user", "admin"})
public Response miEndpoint() {
    // Solo usuarios autenticados
}

@DELETE
@RolesAllowed("admin")
public Response soloAdmin() {
    // Solo administradores
}
```

**Ver guía completa:** [GUIA_JWT.md](Docs/GUIA_JWT.md)

## 🗄️ Base de Datos

### Migraciones con Flyway

Las migraciones se ejecutan automáticamente al iniciar:

```
src/main/resources/db/migration/
├── V1__Initial_schema.sql
├── V2__Add_users.sql (crear siguiente migración)
└── V3__Add_roles.sql
```

### Convención de nombres
- `V{version}__{description}.sql`
- Ejemplo: `V2__Add_users_table.sql`

### Comandos útiles

```bash
# Ver estado de migraciones
./gradlew flywayInfo

# Limpiar BD (CUIDADO: borra todo)
./gradlew flywayClean

# Migrar manualmente
./gradlew flywayMigrate
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Tests con cobertura
./gradlew test jacocoTestReport

# Tests continuos
./gradlew test --continuous
```

### Ejemplo de Test

```java
@QuarkusTest
public class MyEntityResourceTest {

    @Test
    public void testListEndpoint() {
        given()
            .when().get("/api/myentities")
            .then()
            .statusCode(200);
    }
}
```

## 📦 Build y Deploy

### Desarrollo

```bash
./gradlew quarkusDev
```

### Build para Producción

```bash
# Fast JAR (default, recomendado)
./gradlew build

# Uber JAR (todo en un JAR)
./gradlew build -Dquarkus.package.type=uber-jar

# Native (requiere GraalVM)
./gradlew build -Dquarkus.package.type=native
```

### Ejecutar en Producción

```bash
# Configurar perfil de producción
cp .env.prod .env
# Editar .env con valores reales

# Ejecutar
java -jar build/quarkus-app/quarkus-run.jar
```

### Docker

```bash
# Build imagen
docker build -f src/main/docker/Dockerfile.jvm -t learning-quarkus .

# Ejecutar
docker run -p 8080:8080 \
  --env-file .env \
  learning-quarkus
```

## 📖 Documentación Adicional

- [CONFIGURACION_PERFILES.md](Docs/CONFIGURACION_PERFILES.md) - Configuración de perfiles
- [GUIA_JWT.md](Docs/GUIA_JWT.md) - Seguridad y autenticación JWT
- [API_USAGE.md](Docs/API_USAGE.md) - Ejemplos de uso de la API
- [CONFIGURACION_SECRETOS.md](Docs/CONFIGURACION_SECRETOS.md) - Gestión de secretos

## 🔧 Variables de Entorno Principales

```bash
# Perfil
QUARKUS_PROFILE=dev                    # dev o prod

# Base de Datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=quarkus_dev
DB_USER=postgres
DB_PASSWORD=postgres

# JWT
JWT_ENABLED=true
JWT_ISSUER=https://learning-quarkus-dev
JWT_DURATION=3600

# Servidor
SERVER_PORT=8080
```

**Ver lista completa en:** `.env.dev` y `.env.prod`

## 🛠️ Stack Tecnológico

| Categoría | Tecnología |
|-----------|------------|
| **Framework** | Quarkus 3.30.6 |
| **Java** | Java 21 |
| **Build** | Gradle 8.12 |
| **Database** | PostgreSQL 14+ |
| **ORM** | Hibernate ORM Panache |
| **Migrations** | Flyway |
| **Security** | SmallRye JWT |
| **Validation** | Hibernate Validator |
| **Documentation** | OpenAPI/Swagger |
| **Observability** | Micrometer, Prometheus |
| **Testing** | JUnit 5, RestAssured |

## 🤝 Contribuir

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/nueva-caracteristica`)
3. Commit cambios (`git commit -m 'Agregar nueva característica'`)
4. Push a la rama (`git push origin feature/nueva-caracteristica`)
5. Abrir Pull Request

## 📝 Licencia

Este proyecto es de código abierto bajo la licencia Apache 2.0.

## 🆘 Soporte

- 📖 [Documentación de Quarkus](https://quarkus.io/guides/)
- 💬 [Quarkus Chat](https://quarkusio.zulipchat.com/)
- 🐛 [Issues](https://github.com/tu-usuario/learning-quarkus/issues)

---

Hecho con ❤️ usando Quarkus

