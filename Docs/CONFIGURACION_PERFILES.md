# 🔧 Configuración con Perfiles de Quarkus

## 📋 Estructura de Configuración

El proyecto ahora usa un sistema de perfiles simplificado con solo 2 archivos `.env`:

```
.env              → Archivo local (gitignored)
.env.dev          → Template para desarrollo/testing
.env.prod         → Template para producción
```

## 🎯 Archivos de Configuración

### application.yml (Base)
Configuración común para todos los entornos:
- Estructura de la aplicación
- Configuración de base de datos (con variables)
- CORS, Swagger, Health Checks
- Métricas, Logging, Mailer
- JWT y OIDC

### application-dev.yml
Configuración específica para desarrollo:
- SQL logging activado
- Swagger UI habilitado
- Logging en nivel DEBUG
- Mailer en modo mock
- Sin verificación de email

### application-prod.yml
Configuración específica para producción:
- SQL logging desactivado
- Swagger UI deshabilitado
- Logging en nivel INFO (formato JSON)
- Mailer real con SMTP
- Seguridad completa activada

## 🚀 Cómo Usar

### 1. Para Desarrollo Local

```bash
# Copiar template de desarrollo
cp .env.dev .env

# O editar manualmente y asegurarse de tener:
# QUARKUS_PROFILE=dev

# Ejecutar aplicación
./gradlew quarkusDev
```

### 2. Para Testing

El perfil `dev` también se usa para testing:

```bash
# Los tests automáticamente usan perfil dev
./gradlew test
```

### 3. Para Producción

```bash
# Copiar template de producción
cp .env.prod .env

# Editar .env y configurar valores reales:
# - Credenciales de base de datos
# - Secretos JWT
# - Configuración SMTP
# - URLs de OIDC si aplica

# Asegurarse de tener:
# QUARKUS_PROFILE=prod

# Compilar para producción
./gradlew build -Dquarkus.package.jar.enabled=true -Dquarkus.package.jar.type=uber-jar

# Ejecutar
java -jar build/quarkus-app/quarkus-run.jar
```

## 🔐 Variables de Entorno Importantes

### Control de Perfil
```bash
QUARKUS_PROFILE=dev   # O 'prod'
```

### Base de Datos
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=quarkus_dev   # O quarkus_prod
DB_USER=postgres
DB_PASSWORD=postgres
DB_POOL_MIN_SIZE=5
DB_POOL_MAX_SIZE=20
```

### Servidor
```bash
SERVER_PORT=8080
SERVER_HOST=0.0.0.0
```

### JWT (Seguridad)
```bash
JWT_ENABLED=true
JWT_SECRET=tu-secret-aqui-min-256-bits
JWT_ISSUER=https://learning-quarkus-dev
JWT_DURATION=3600
```

### OIDC (Opcional)
```bash
OIDC_ENABLED=false
OIDC_AUTH_SERVER_URL=
OIDC_CLIENT_ID=
OIDC_CLIENT_SECRET=
```

### Email
```bash
MAIL_FROM=noreply@example.com
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_MOCK=true  # false en producción
```

### Logging
```bash
LOG_LEVEL=DEBUG       # INFO en producción
LOG_JSON_ENABLED=false # true en producción
```

## 📝 Flujo de Carga de Configuración

1. **Gradle carga `.env`** → `build.gradle` lee las variables
2. **Se detecta `QUARKUS_PROFILE`** → Determina qué perfil usar
3. **Quarkus carga configuraciones en orden:**
   - `application.yml` (base)
   - `application-{profile}.yml` (sobrescribe base)
   - Variables de entorno (sobrescribe todo)

## 🔍 Verificar Configuración Activa

```bash
# Ver perfil activo
./gradlew quarkusDev

# Salida mostrará:
# ✅ Variables de entorno cargadas desde .env
# 📋 Perfil de Quarkus activo: dev

# Acceder a endpoint de configuración
curl http://localhost:8080/api/config/info
```

## 🛡️ Seguridad en Producción

### ⚠️ NUNCA SUBIR A GIT:
- `.env` con valores reales
- `privateKey.pem` si contiene claves privadas reales
- Credenciales de base de datos
- Secretos JWT/OIDC

### ✅ BUENAS PRÁCTICAS:
1. Usar gestores de secretos en producción:
   - AWS Secrets Manager
   - Azure Key Vault
   - HashiCorp Vault
   - Kubernetes Secrets

2. Rotar secretos regularmente

3. Usar diferentes claves JWT para cada entorno

4. Habilitar HTTPS/TLS en producción

## 🔧 Migraciones de Base de Datos

Las migraciones ahora usan nomenclatura simplificada:

```
V1__Initial_schema.sql
V2__Add_users_table.sql
V3__Add_roles_table.sql
```

Flyway se ejecuta automáticamente al iniciar la aplicación.

## 📦 Compilación por Perfil

```bash
# Desarrollo (con hot-reload)
./gradlew quarkusDev

# Testing
./gradlew test

# Producción (uber-jar)
./gradlew build -Dquarkus.package.jar.enabled=true -Dquarkus.package.jar.type=uber-jar

# Producción (fast-jar, default)
./gradlew build

# Native (requiere GraalVM)
./gradlew build -Dquarkus.native.enabled=true
```

## 🐳 Docker

```bash
# Desarrollo
docker-compose up

# Producción
docker build -f src/main/docker/Dockerfile.jvm -t learning-quarkus .
docker run -p 8080:8080 learning-quarkus
```

## 🆘 Solución de Problemas

### Perfil no se aplica
```bash
# Verificar que QUARKUS_PROFILE está en .env
cat .env | grep QUARKUS_PROFILE

# Forzar perfil manualmente
./gradlew quarkusDev -Dquarkus.profile=dev
```

### Variables no se cargan
```bash
# Verificar sintaxis del .env
cat .env | grep -v '^#' | grep '='

# Limpiar y reconstruir
./gradlew clean build
```

### Error de migraciones
```bash
# Limpiar base de datos
# CUIDADO: Elimina todos los datos
psql -U postgres -d quarkus_dev -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

# Reiniciar aplicación
./gradlew quarkusDev
```

## 📚 Referencias

- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [Quarkus Profiles](https://quarkus.io/guides/config-reference#profiles)
- [SmallRye JWT](https://quarkus.io/guides/security-jwt)
- [Flyway Migrations](https://quarkus.io/guides/flyway)

