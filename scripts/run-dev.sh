#!/bin/bash

# Script para ejecutar la aplicación en modo desarrollo con el entorno correcto

ENV=${1:-development}

echo "🚀 Iniciando aplicación Quarkus en modo desarrollo"
echo "Entorno: $ENV"
echo ""

# Cargar el entorno
./scripts/load-env.sh "$ENV"

# Iniciar PostgreSQL si no está corriendo
if ! docker ps | grep -q postgres-quarkus; then
    echo "📦 Iniciando PostgreSQL..."
    docker-compose up -d
    echo "⏳ Esperando a que PostgreSQL esté listo..."
    sleep 5
fi

# Ejecutar Quarkus en modo desarrollo
echo ""
echo "🔥 Iniciando Quarkus Dev Mode..."
echo "   Endpoints disponibles en: http://localhost:${SERVER_PORT:-8080}"
echo "   - API: http://localhost:${SERVER_PORT:-8080}/api"
echo "   - Swagger: http://localhost:${SERVER_PORT:-8080}/swagger-ui"
echo "   - Dev UI: http://localhost:${SERVER_PORT:-8080}/q/dev"
echo ""

./gradlew quarkusDev

