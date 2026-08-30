#!/bin/bash

# ============================================================================
# COMANDOS ESENCIALES - Learning Quarkus
# ============================================================================

cat << 'EOF'
╔═══════════════════════════════════════════════════════╗
║   🚀 Learning Quarkus - Comandos Esenciales          ║
╚═══════════════════════════════════════════════════════╝

📋 CONFIGURACIÓN INICIAL (Solo una vez)
───────────────────────────────────────────────────────

1. Copiar variables de entorno:
   $ cp .env.dev .env

2. Verificar perfil activo:
   $ cat .env | grep QUARKUS_PROFILE
   → Debe mostrar: QUARKUS_PROFILE=dev

───────────────────────────────────────────────────────
🐘 BASE DE DATOS
───────────────────────────────────────────────────────

# Iniciar PostgreSQL con Docker
$ docker-compose up -d

# Verificar que esté corriendo
$ docker ps | grep postgres

# Ver logs de PostgreSQL
$ docker-compose logs -f postgres

# Detener PostgreSQL
$ docker-compose down

───────────────────────────────────────────────────────
🔧 DESARROLLO
───────────────────────────────────────────────────────

# Iniciar en modo desarrollo (hot-reload)
$ ./gradlew quarkusDev

# Limpiar build
$ ./gradlew clean

# Compilar sin tests
$ ./gradlew build -x test

# Compilar con tests
$ ./gradlew build

───────────────────────────────────────────────────────
🎭 PERFILES
───────────────────────────────────────────────────────

# Ver perfil actual
$ ./scripts/switch-profile.sh info

# Cambiar a desarrollo
$ ./scripts/switch-profile.sh dev

# Cambiar a producción
$ ./scripts/switch-profile.sh prod

───────────────────────────────────────────────────────
🔐 JWT - AUTENTICACIÓN
───────────────────────────────────────────────────────

# Login como admin
$ curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Login como user
$ curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user"}'

# Usar token (reemplazar <TOKEN>)
$ curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <TOKEN>"

───────────────────────────────────────────────────────
🧪 TESTING
───────────────────────────────────────────────────────

# Ejecutar todos los tests
$ ./gradlew test

# Tests con logs
$ ./gradlew test --info

# Tests continuos (watch mode)
$ ./gradlew test --continuous

# Tests de un archivo específico
$ ./gradlew test --tests "MyEntityResourceTest"

───────────────────────────────────────────────────────
🏗️ BUILD PARA PRODUCCIÓN
───────────────────────────────────────────────────────

# 1. Cambiar a perfil producción
$ ./scripts/switch-profile.sh prod

# 2. Editar .env con valores reales
$ nano .env

# 3. Build uber-jar
$ ./gradlew build -Dquarkus.package.jar.enabled=true -Dquarkus.package.jar.type=uber-jar

# 4. Ejecutar
$ java -jar build/quarkus-app/quarkus-run.jar

───────────────────────────────────────────────────────
🐳 DOCKER
───────────────────────────────────────────────────────

# Build imagen
$ docker build -f src/main/docker/Dockerfile.jvm \
  -t learning-quarkus .

# Ejecutar
$ docker run -p 8080:8080 --env-file .env learning-quarkus

# Ver logs
$ docker logs -f <container_id>

───────────────────────────────────────────────────────
📊 ENDPOINTS ÚTILES
───────────────────────────────────────────────────────

Swagger UI:       http://localhost:8080/swagger-ui
Health Check:     http://localhost:8080/health
Métricas:         http://localhost:8080/metrics
Dev UI:           http://localhost:8080/q/dev (solo en dev mode)

───────────────────────────────────────────────────────
🗄️ MIGRACIONES FLYWAY
───────────────────────────────────────────────────────

# Crear nueva migración
$ echo "CREATE TABLE ..." > src/main/resources/db/migration/V2__Add_users.sql

# Ver estado de migraciones
$ ./gradlew flywayInfo

# Limpiar BD (CUIDADO: borra todo)
$ ./gradlew flywayClean

───────────────────────────────────────────────────────
🔍 DEBUGGING
───────────────────────────────────────────────────────

# Ver configuración activa
$ curl http://localhost:8080/api/config/info

# Ver logs en tiempo real
$ tail -f build/quarkus.log

# Ejecutar con más logs
$ ./gradlew quarkusDev -Dquarkus.log.level=DEBUG

───────────────────────────────────────────────────────
📦 EXTENSIONES
───────────────────────────────────────────────────────

# Listar extensiones disponibles
$ ./gradlew listExtensions

# Buscar extensión
$ ./gradlew listExtensions | grep -i "cache"

# Agregar extensión
$ ./gradlew addExtension --extensions="micrometer-registry-prometheus"

───────────────────────────────────────────────────────
🆘 SOLUCIÓN DE PROBLEMAS
───────────────────────────────────────────────────────

# App no inicia
$ ./gradlew clean build
$ ./gradlew quarkusDev

# Error de BD
$ docker-compose down
$ docker-compose up -d

# Resetear migraciones (CUIDADO)
$ psql -U postgres -d quarkus_dev -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

# Ver errores de compilación
$ ./gradlew build --stacktrace

───────────────────────────────────────────────────────
📚 DOCUMENTACIÓN
───────────────────────────────────────────────────────

QUICK_START.md              → Inicio rápido
CONFIGURACION_PERFILES.md   → Guía de perfiles
GUIA_JWT.md                 → Seguridad JWT
ANALISIS_DEPENDENCIAS.md    → Extensiones disponibles
COMPLETADO.md               → Estado final

───────────────────────────────────────────────────────
✅ CHECKLIST RÁPIDO
───────────────────────────────────────────────────────

□ .env copiado desde .env.dev
□ PostgreSQL corriendo
□ ./gradlew quarkusDev inicia sin errores
□ http://localhost:8080/health retorna UP
□ http://localhost:8080/swagger-ui se abre
□ Login funciona correctamente

───────────────────────────────────────────────────────
🎉 ¡Listo para desarrollar!
───────────────────────────────────────────────────────

EOF

