# 🔐 Guía de Configuración y Secretos

Esta guía explica cómo manejar la configuración y secretos en tu aplicación Quarkus.

## 📋 Tabla de Contenidos

- [Archivos de Configuración](#archivos-de-configuración)
- [Variables de Entorno](#variables-de-entorno)
- [Uso en Desarrollo](#uso-en-desarrollo)
- [Uso en Producción](#uso-en-producción)
- [Mejores Prácticas](#mejores-prácticas)

## 📁 Archivos de Configuración

### Estructura de Archivos

```
learning-quarkus/
├── .env                    # Archivo actual (git ignored)
├── .env.example           # Template con todas las variables
├── .env.development       # Configuración para desarrollo
├── .env.test             # Configuración para tests
├── .env.production       # Template para producción
└── src/main/resources/
    └── application.yml   # Configuración base de Quarkus
```

### Jerarquía de Configuración

1. **Variables de entorno del sistema** (mayor prioridad)
2. **Archivo `.env`** (cargado por Gradle)
3. **`application.yml`** con valores por defecto

## 🔧 Variables de Entorno

### Variables Principales

#### Aplicación
```bash
APP_ENV=development        # Entorno: development, test, production
APP_NAME=learning-quarkus  # Nombre de la aplicación
```

#### Servidor
```bash
SERVER_PORT=8080          # Puerto del servidor HTTP
SERVER_HOST=0.0.0.0       # Host del servidor
```

#### Base de Datos
```bash
DB_HOST=localhost         # Host de PostgreSQL
DB_PORT=5432             # Puerto de PostgreSQL
DB_NAME=quarkus_db       # Nombre de la base de datos
DB_USER=postgres         # Usuario de la base de datos
DB_PASSWORD=postgres     # ⚠️ Contraseña (usar secrets manager en prod)
DB_POOL_MIN_SIZE=5       # Tamaño mínimo del pool de conexiones
DB_POOL_MAX_SIZE=20      # Tamaño máximo del pool de conexiones
```

#### CORS
```bash
CORS_ORIGINS=http://localhost:3000,http://localhost:4200
```

#### Seguridad - JWT
```bash
JWT_SECRET=your-secret-key    # ⚠️ Secret key (CAMBIAR en producción)
JWT_ISSUER=https://your-domain.com
JWT_DURATION=3600            # Duración en segundos
```

#### Email
```bash
MAIL_FROM=noreply@example.com
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USERNAME=             # Opcional
MAIL_PASSWORD=             # ⚠️ Opcional
MAIL_MOCK=true            # true para desarrollo
```

#### Logging
```bash
LOG_LEVEL=INFO            # TRACE, DEBUG, INFO, WARN, ERROR
LOG_JSON_ENABLED=false    # true para logs en formato JSON
```

## 💻 Uso en Desarrollo

### 1. Configuración Inicial

```bash
# Copiar el archivo de ejemplo
cp .env.example .env

# O usar la configuración de desarrollo
cp .env.development .env

# Editar según tus necesidades
nano .env
```

### 2. Ejecutar la Aplicación

#### Opción 1: Script automático
```bash
# Usa .env.development automáticamente
./scripts/run-dev.sh

# O especificar otro entorno
./scripts/run-dev.sh test
```

#### Opción 2: Manual
```bash
# Cargar el entorno
./scripts/load-env.sh development

# Iniciar PostgreSQL
docker-compose up -d

# Ejecutar Quarkus
./gradlew quarkusDev
```

#### Opción 3: Directo con Gradle
```bash
./gradlew quarkusDev
```

### 3. Cambiar de Entorno

```bash
# Cambiar a desarrollo
./scripts/load-env.sh development

# Cambiar a test
./scripts/load-env.sh test

# Ver entornos disponibles
ls -1 .env.*
```

## 🚀 Uso en Producción

### Opción 1: Variables de Entorno del Sistema (Recomendado)

En producción, **NO uses archivos `.env`**. En su lugar, configura las variables directamente en el sistema:

#### Docker / Docker Compose
```yaml
# docker-compose.prod.yml
services:
  app:
    image: quarkus/learning-quarkus
    environment:
      - DB_HOST=postgres-prod
      - DB_NAME=${DATABASE_NAME}
      - DB_USER=${DATABASE_USER}
      - DB_PASSWORD=${DATABASE_PASSWORD}
      - JWT_SECRET=${JWT_SECRET_KEY}
    env_file:
      - .env.production  # Solo si es necesario
```

#### Kubernetes
```yaml
# deployment.yml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  db-password: <base64-encoded>
  jwt-secret: <base64-encoded>
---
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: app
        env:
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: db-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: jwt-secret
```

#### Systemd Service
```ini
# /etc/systemd/system/quarkus-app.service
[Service]
Environment="DB_HOST=localhost"
Environment="DB_PASSWORD=secure-password"
EnvironmentFile=/etc/quarkus-app/secrets.env
ExecStart=/usr/bin/java -jar /opt/app/quarkus-run.jar
```

### Opción 2: Secrets Managers

#### AWS Secrets Manager
```bash
# Obtener secretos en tiempo de ejecución
export DB_PASSWORD=$(aws secretsmanager get-secret-value \
  --secret-id prod/db/password \
  --query SecretString \
  --output text)
```

#### HashiCorp Vault
```bash
# Integrar con Vault
vault kv get -field=password secret/database
```

### Opción 3: Configuración de Quarkus para Producción

Crea un `application-prod.yml`:

```yaml
# src/main/resources/application-prod.yml
quarkus:
  datasource:
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  log:
    level: INFO
    console:
      json: true
```

Ejecuta con el perfil de producción:
```bash
java -Dquarkus.profile=prod -jar quarkus-run.jar
```

## 🔒 Mejores Prácticas

### ✅ DO (Hacer)

1. **Usar `.env.example`** como referencia
   ```bash
   cp .env.example .env
   ```

2. **Mantener `.env` en `.gitignore`**
   - ✅ Ya está configurado en este proyecto

3. **Usar valores por defecto seguros en `application.yml`**
   ```yaml
   datasource:
     username: ${DB_USER:postgres}  # Valor por defecto
   ```

4. **Rotar secretos regularmente** en producción

5. **Usar diferentes secretos por entorno**
   - Desarrollo: secretos simples
   - Producción: secretos fuertes y únicos

6. **Validar configuración al inicio**
   ```java
   @ConfigMapping(prefix = "database")
   public interface DatabaseConfig {
       @NotBlank String host();
       @NotBlank String password();
   }
   ```

### ❌ DON'T (No Hacer)

1. **❌ Nunca subir `.env` a git**
   ```bash
   # Verificar que no esté trackeado
   git status | grep .env
   ```

2. **❌ No hardcodear secretos en el código**
   ```java
   // ❌ MAL
   String password = "mi-password-123";
   
   // ✅ BIEN
   @ConfigProperty(name = "db.password")
   String password;
   ```

3. **❌ No usar los mismos secretos en todos los entornos**

4. **❌ No loggear secretos**
   ```java
   // ❌ MAL
   log.info("Password: {}", password);
   
   // ✅ BIEN
   log.info("Connecting to database...");
   ```

5. **❌ No compartir `.env` por email/slack**

## 🧪 Testing

Para tests, usa `.env.test` o sobrescribe variables:

```bash
# En tu CI/CD
export DB_HOST=localhost
export DB_NAME=test_db
./gradlew test
```

O en el código:
```java
@QuarkusTest
@TestProfile(CustomTestProfile.class)
public class MyTest {
    // ...
}

public class CustomTestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.datasource.jdbc.url", "jdbc:h2:mem:test"
        );
    }
}
```

## 📦 Generación de Secretos Seguros

```bash
# JWT Secret (32 bytes en base64)
openssl rand -base64 32

# Password fuerte
openssl rand -base64 24

# UUID
uuidgen
```

## 🔍 Debugging de Configuración

```bash
# Ver configuración actual en runtime
curl http://localhost:8080/q/dev/io.quarkus.quarkus-config/config

# Ver propiedades cargadas
./gradlew quarkusDev -Dquarkus.config.log.values=true

# Ver todas las variables de entorno
env | grep -E "^(DB_|SERVER_|APP_)"
```

## 📚 Referencias

- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [12-Factor App Configuration](https://12factor.net/config)
- [OWASP Secrets Management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)

---

## 🆘 Solución de Problemas

### Variables no se cargan

1. Verifica que el plugin dotenv esté en `build.gradle`
2. Verifica que el archivo `.env` existe
3. Reinicia el proceso de Gradle
4. Verifica la sintaxis (sin espacios alrededor del `=`)

### Error de conexión a la base de datos

1. Verifica que PostgreSQL esté corriendo: `docker ps`
2. Verifica las variables: `echo $DB_HOST $DB_PORT`
3. Verifica la URL en los logs de Quarkus

### Secretos expuestos en logs

1. Configura `quarkus.log.level=INFO` (no DEBUG en producción)
2. Usa `@ConfigProperty` y no loggees su valor
3. Revisa los logs antes de compartir

