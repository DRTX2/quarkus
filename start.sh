#!/bin/bash

echo "🚀 Iniciando aplicación Quarkus..."
echo ""

# Cargar entorno (por defecto development)
ENV=${1:-development}

# Verificar si existe el script de carga de entorno
if [ -f "./scripts/load-env.sh" ]; then
    ./scripts/load-env.sh "$ENV"
else
    echo "⚠️  Script de carga de entorno no encontrado, usando .env actual"
fi

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo. Por favor, inicia Docker primero."
    exit 1
fi

# Iniciar PostgreSQL con Docker Compose
echo "📦 Iniciando PostgreSQL..."
docker-compose up -d

# Esperar a que PostgreSQL esté listo
echo "⏳ Esperando a que PostgreSQL esté listo..."
sleep 5

# Iniciar Quarkus en modo desarrollo
echo "🔥 Iniciando Quarkus en modo desarrollo..."
echo ""
echo "📝 Endpoints disponibles:"
echo "   - API: http://localhost:${SERVER_PORT:-8080}/api"
echo "   - Swagger UI: http://localhost:${SERVER_PORT:-8080}/swagger-ui"
echo "   - Dev UI: http://localhost:${SERVER_PORT:-8080}/q/dev"
echo "   - Health: http://localhost:${SERVER_PORT:-8080}/health"
echo "   - Metrics: http://localhost:${SERVER_PORT:-8080}/metrics"
echo ""

./gradlew quarkusDev

