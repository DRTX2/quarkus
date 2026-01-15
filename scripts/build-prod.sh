#!/bin/bash

# Script para construir y ejecutar la aplicación para producción

ENV=${1:-production}

echo "🏗️  Construyendo aplicación para producción"
echo "Entorno: $ENV"
echo ""

# Cargar el entorno
./scripts/load-env.sh "$ENV"

# Construir la aplicación
echo "📦 Construyendo aplicación..."
./gradlew clean build

if [ $? -ne 0 ]; then
    echo "❌ Error en la construcción"
    exit 1
fi

echo ""
echo "✅ Construcción exitosa"
echo ""
echo "Para ejecutar la aplicación:"
echo "  java -jar build/quarkus-app/quarkus-run.jar"
echo ""
echo "O construir imagen Docker:"
echo "  docker build -f src/main/docker/Dockerfile.jvm -t quarkus/learning-quarkus ."
echo "  docker run -i --rm -p 8080:8080 quarkus/learning-quarkus"

