# 🎯 Ejemplos de Uso de Variables de Entorno

## 📖 Tabla de Contenidos

1. [Configuración Básica](#configuración-básica)
2. [En Código Java](#en-código-java)
3. [Diferentes Entornos](#diferentes-entornos)
4. [Casos de Uso Comunes](#casos-de-uso-comunes)

## 🔧 Configuración Básica

### 1. Copiar template y configurar

```bash
# Copiar el ejemplo
cp .env.example .env

# Editar con tus valores
nano .env
```

### 2. Ejemplo de .env para desarrollo local

```bash
# .env
APP_ENV=development
SERVER_PORT=8080

DB_HOST=localhost
DB_PORT=5432
DB_NAME=mi_base_datos
DB_USER=mi_usuario
DB_PASSWORD=mi_password

LOG_LEVEL=DEBUG
CORS_ORIGINS=http://localhost:3000
```

## 💻 En Código Java

### Opción 1: Usando @ConfigProperty

```java
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.inject.Inject;

public class MyService {
    
    // Variable simple
    @ConfigProperty(name = "quarkus.http.port")
    int serverPort;
    
    // Con valor por defecto
    @ConfigProperty(name = "app.custom-setting", defaultValue = "default")
    String customSetting;
    
    // Opcional
    @ConfigProperty(name = "app.optional-setting")
    Optional<String> optionalSetting;
    
    public void printConfig() {
        System.out.println("Server port: " + serverPort);
        System.out.println("Custom: " + customSetting);
    }
}
```

### Opción 2: Usando @ConfigMapping (Recomendado para grupos)

```java
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "database")
public interface DatabaseConfig {
    String host();
    int port();
    String name();
    String user();
    String password();
}

// Uso
@Inject
DatabaseConfig dbConfig;

String url = "jdbc:postgresql://" + 
    dbConfig.host() + ":" + 
    dbConfig.port() + "/" + 
    dbConfig.name();
```

### Opción 3: Programático con Config

```java
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class ConfigExample {
    public void readConfig() {
        Config config = ConfigProvider.getConfig();
        
        String dbHost = config.getValue("DB_HOST", String.class);
        Integer dbPort = config.getOptionalValue("DB_PORT", Integer.class)
                              .orElse(5432);
        
        System.out.println("DB: " + dbHost + ":" + dbPort);
    }
}
```

## 🌍 Diferentes Entornos

### Desarrollo Local

```bash
# Usar configuración de desarrollo
./scripts/load-env.sh development
./gradlew quarkusDev
```

**Archivo .env.development:**
```bash
APP_ENV=development
DB_HOST=localhost
DB_PASSWORD=simple_password
LOG_LEVEL=DEBUG
MAIL_MOCK=true
```

### Testing

```bash
# Usar configuración de test
./scripts/load-env.sh test
./gradlew test
```

**Archivo .env.test:**
```bash
APP_ENV=test
DB_HOST=localhost
DB_NAME=test_db
LOG_LEVEL=WARN
MAIL_MOCK=true
```

### Producción con Docker

```bash
# NO usar archivo .env
# Pasar variables directamente
docker run -d \
  -e APP_ENV=production \
  -e DB_HOST=prod-db-server \
  -e DB_PASSWORD=${SECURE_DB_PASSWORD} \
  -e JWT_SECRET=${SECURE_JWT_SECRET} \
  -p 8080:8080 \
  quarkus/learning-quarkus
```

### Producción con Docker Compose

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  app:
    image: quarkus/learning-quarkus
    environment:
      APP_ENV: production
      DB_HOST: postgres
      DB_NAME: ${PROD_DB_NAME}
      DB_USER: ${PROD_DB_USER}
      DB_PASSWORD: ${PROD_DB_PASSWORD}
      JWT_SECRET: ${PROD_JWT_SECRET}
    depends_on:
      - postgres
  
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: ${PROD_DB_NAME}
      POSTGRES_USER: ${PROD_DB_USER}
      POSTGRES_PASSWORD: ${PROD_DB_PASSWORD}
```

```bash
# Ejecutar
docker-compose -f docker-compose.prod.yml up -d
```

## 📋 Casos de Uso Comunes

### Caso 1: Cambiar Puerto del Servidor

```bash
# En .env
echo "SERVER_PORT=9090" >> .env

# O temporalmente
SERVER_PORT=9090 ./gradlew quarkusDev
```

### Caso 2: Conectar a Base de Datos Remota

```bash
# En .env
DB_HOST=192.168.1.100
DB_PORT=5432
DB_NAME=produccion_db
DB_USER=app_user
DB_PASSWORD=secure_password
```

### Caso 3: Habilitar/Deshabilitar Features

```bash
# En .env
APP_FEATURES_REGISTRATION_ENABLED=false
APP_FEATURES_EMAIL_VERIFICATION_REQUIRED=true
```

En application.yml:
```yaml
app:
  features:
    registration-enabled: ${APP_FEATURES_REGISTRATION_ENABLED:true}
    email-verification-required: ${APP_FEATURES_EMAIL_VERIFICATION_REQUIRED:true}
```

### Caso 4: Configurar CORS Dinámicamente

```bash
# Desarrollo - permitir frontend local
CORS_ORIGINS=http://localhost:3000,http://localhost:4200

# Producción - solo dominio específico
CORS_ORIGINS=https://miapp.com,https://www.miapp.com
```

### Caso 5: Niveles de Log por Entorno

```bash
# Desarrollo
LOG_LEVEL=DEBUG
LOG_JSON_ENABLED=false

# Producción
LOG_LEVEL=INFO
LOG_JSON_ENABLED=true
```

### Caso 6: Email en Desarrollo vs Producción

```bash
# Desarrollo - modo mock
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_MOCK=true

# Producción - servidor real
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=app@company.com
MAIL_PASSWORD=app_password
MAIL_MOCK=false
```

## 🔐 Secretos Seguros

### Generar JWT Secret

```bash
# Generar un secret fuerte
openssl rand -base64 32

# Agregar a .env
echo "JWT_SECRET=$(openssl rand -base64 32)" >> .env
```

### Usar en el código

```java
@ConfigProperty(name = "jwt.secret")
String jwtSecret;

// Usar para firmar tokens
// NUNCA loggear este valor
```

## 🧪 Testing con Diferentes Configuraciones

### Test con configuración custom

```java
@QuarkusTest
@TestProfile(CustomTestProfile.class)
public class MyServiceTest {
    
    @Test
    public void testWithCustomConfig() {
        // Test usa configuración de CustomTestProfile
    }
}

public class CustomTestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "db.host", "test-db",
            "db.port", "5433",
            "mail.mock", "true"
        );
    }
}
```

## 🚀 Despliegue

### Kubernetes ConfigMap y Secrets

```yaml
# configmap.yml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  SERVER_PORT: "8080"
  DB_HOST: "postgres-service"
  LOG_LEVEL: "INFO"

---
# secret.yml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
stringData:
  DB_PASSWORD: "secure-password"
  JWT_SECRET: "secure-jwt-secret"

---
# deployment.yml
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: app
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: app-secrets
```

```bash
kubectl apply -f configmap.yml
kubectl apply -f secret.yml
kubectl apply -f deployment.yml
```

## 💡 Tips y Trucos

### Ver todas las variables cargadas

```bash
# Ver .env actual
cat .env

# Ver solo variables específicas
cat .env | grep DB_

# Ver en runtime (Dev Mode)
curl http://localhost:8080/q/dev/io.quarkus.quarkus-config/config
```

### Sobrescribir temporalmente

```bash
# Solo para esta ejecución
DB_NAME=otra_db ./gradlew quarkusDev

# Múltiples variables
DB_HOST=192.168.1.100 DB_PORT=5433 ./gradlew quarkusDev
```

### Validar configuración

```bash
# Ver qué variables están siendo usadas
./gradlew quarkusDev -Dquarkus.config.log.values=true

# Test de conexión rápido
./scripts/load-env.sh development
./gradlew quarkusDev -Dquarkus.hibernate-orm.database.generation=validate
```

### Backup de configuración

```bash
# Guardar configuración actual
cp .env .env.backup

# Restaurar
cp .env.backup .env
```

## 📚 Referencias

- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [MicroProfile Config](https://github.com/eclipse/microprofile-config)
- [12-Factor App Config](https://12factor.net/config)

---

**Documentación completa**: Ver [CONFIGURACION_SECRETOS.md](CONFIGURACION_SECRETOS.md)

