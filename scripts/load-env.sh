#!/bin/bash
fi
    exec "$@"
    echo "🚀 Ejecutando: $@"
    shift
if [ $# -gt 1 ]; then
# Si se especifica un segundo argumento, ejecutar comando

echo ""
grep -E "^(APP_ENV|SERVER_PORT|DB_NAME|LOG_LEVEL)=" .env | sed 's/^/  /'
echo "📋 Configuración cargada:"
echo ""
# Mostrar algunas variables cargadas (sin mostrar secretos)

echo "✅ Archivo .env actualizado con configuración de $ENV"
cp ".env.$ENV" ".env"
# Copiar el archivo de entorno correspondiente

fi
    exit 1
    ls -1 .env.* 2>/dev/null | sed 's/\.env\./  - /'
    echo "Entornos disponibles:"
    echo "❌ Error: El archivo .env.$ENV no existe"
if [ ! -f ".env.$ENV" ]; then
# Verificar que el archivo de entorno existe

echo "🔧 Cargando configuración de entorno: $ENV"

ENV=${1:-development}

# Script para cargar el entorno correcto antes de ejecutar la aplicación


