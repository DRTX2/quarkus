#!/bin/bash

# Script para cambiar entre perfiles de .env
# Uso: ./scripts/switch-env.sh [dev|prod]

PROFILE=${1:-dev}
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

case $PROFILE in
  dev|development)
    echo "🔧 Cambiando a perfil de DESARROLLO..."
    cp "$PROJECT_ROOT/.env.development" "$PROJECT_ROOT/.env"
    echo "✅ Perfil de desarrollo activado"
    echo "📝 Variables cargadas desde .env.development"
    ;;
  prod|production)
    echo "🚀 Cambiando a perfil de PRODUCCIÓN..."
    cp "$PROJECT_ROOT/.env.production" "$PROJECT_ROOT/.env"
    echo "✅ Perfil de producción activado"
    echo "⚠️  ADVERTENCIA: Verifica que las variables de producción estén configuradas correctamente"
    ;;
  *)
    echo "❌ Perfil no válido: $PROFILE"
    echo "Uso: ./scripts/switch-env.sh [dev|prod]"
    exit 1
    ;;
esac

echo ""
echo "Perfil actual:"
grep "QUARKUS_PROFILE=" "$PROJECT_ROOT/.env" | head -1

