#!/bin/bash

# Script para cambiar entre perfiles de desarrollo y producción

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Banner
echo -e "${BLUE}"
echo "╔═══════════════════════════════════════════════════╗"
echo "║   🔧 Quarkus Profile Manager                     ║"
echo "╚═══════════════════════════════════════════════════╝"
echo -e "${NC}"

# Función para mostrar uso
show_usage() {
    echo -e "${YELLOW}Uso:${NC}"
    echo "  ./scripts/switch-profile.sh [dev|prod|info]"
    echo ""
    echo -e "${YELLOW}Opciones:${NC}"
    echo "  dev   - Cambiar a perfil de desarrollo"
    echo "  prod  - Cambiar a perfil de producción"
    echo "  info  - Mostrar perfil actual"
    echo ""
    exit 1
}

# Función para mostrar perfil actual
show_info() {
    if [ -f .env ]; then
        CURRENT_PROFILE=$(grep "QUARKUS_PROFILE=" .env | cut -d '=' -f2)
        echo -e "${GREEN}📋 Perfil actual:${NC} ${BLUE}${CURRENT_PROFILE}${NC}"

        DB_NAME=$(grep "DB_NAME=" .env | cut -d '=' -f2)
        echo -e "${GREEN}🗄️  Base de datos:${NC} ${DB_NAME}"

        LOG_LEVEL=$(grep "LOG_LEVEL=" .env | cut -d '=' -f2)
        echo -e "${GREEN}📝 Log level:${NC} ${LOG_LEVEL}"

        MAIL_MOCK=$(grep "MAIL_MOCK=" .env | cut -d '=' -f2)
        echo -e "${GREEN}📧 Email mock:${NC} ${MAIL_MOCK}"
    else
        echo -e "${RED}❌ Archivo .env no encontrado${NC}"
        exit 1
    fi
}

# Función para cambiar a desarrollo
switch_to_dev() {
    echo -e "${YELLOW}🔄 Cambiando a perfil de desarrollo...${NC}"

    if [ ! -f .env.dev ]; then
        echo -e "${RED}❌ Error: .env.dev no encontrado${NC}"
        exit 1
    fi

    cp .env.dev .env
    echo -e "${GREEN}✅ Perfil cambiado a: ${BLUE}dev${NC}"
    echo ""
    echo -e "${YELLOW}Características activadas:${NC}"
    echo "  • SQL logging: ON"
    echo "  • Swagger UI: VISIBLE"
    echo "  • Log level: DEBUG"
    echo "  • Email: MOCK"
    echo "  • Base de datos: quarkus_dev"
    echo ""
    echo -e "${BLUE}💡 Ejecuta: ./gradlew quarkusDev${NC}"
}

# Función para cambiar a producción
switch_to_prod() {
    echo -e "${YELLOW}🔄 Cambiando a perfil de producción...${NC}"

    if [ ! -f .env.prod ]; then
        echo -e "${RED}❌ Error: .env.prod no encontrado${NC}"
        exit 1
    fi

    cp .env.prod .env
    echo -e "${GREEN}✅ Perfil cambiado a: ${BLUE}prod${NC}"
    echo ""
    echo -e "${YELLOW}⚠️  IMPORTANTE - Configurar valores de producción:${NC}"
    echo "  • DB_PASSWORD"
    echo "  • JWT_SECRET"
    echo "  • MAIL_HOST y credenciales"
    echo "  • CORS_ORIGINS"
    echo ""
    echo -e "${RED}⚠️  NO usar valores por defecto en producción${NC}"
    echo ""
    echo -e "${BLUE}Edita .env y luego ejecuta:${NC}"
    echo "  ./gradlew build"
    echo "  java -jar build/quarkus-app/quarkus-run.jar"
}

# Main
case "${1:-}" in
    dev)
        switch_to_dev
        ;;
    prod)
        switch_to_prod
        ;;
    info)
        show_info
        ;;
    *)
        show_usage
        ;;
esac

