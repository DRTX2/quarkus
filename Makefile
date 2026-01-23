.PHONY: help dev build clean test docker-up docker-down switch-dev switch-prod start status

# Colores para la terminal
YELLOW := $(shell tput setaf 3)
GREEN  := $(shell tput setaf 2)
RESET  := $(shell tput sgr0)

help: ## Muestra esta ayuda
	@echo "$(YELLOW)Comandos disponibles:$(RESET)"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-15s$(RESET) %s\n", $$1, $$2}'

# --- Desarrollo ---

dev: ## Inicia la aplicación en modo desarrollo (hot reload)
	./gradlew quarkusDev

start: ## Inicia la infraestructura (Docker) y la aplicación en modo desarrollo
	./start-dev.sh

status: ## Muestra el estado de los contenedores y la aplicación
	docker-compose ps
	@echo "$(YELLOW)URLS:$(RESET)"
	@echo "  API: http://localhost:8080"
	@echo "  Swagger UI: http://localhost:8080/swagger-ui"
	@echo "  Health: http://localhost:8080/q/health"

# --- Construcción y Limpieza ---

build: ## Compila el proyecto y genera el JAR
	./gradlew build -x test

clean: ## Limpia los directorios de compilación
	./gradlew clean

test: ## Ejecuta las pruebas unitarias e integración
	./gradlew test

# --- Infraestructura ---

docker-up: ## Inicia los contenedores de infraestructura (PostgreSQL)
	docker-compose up -d postgres

docker-down: ## Detiene los contenedores de infraestructura
	docker-compose down

docker-logs: ## Muestra los logs de los contenedores
	docker-compose logs -f

# --- Configuración de Entorno ---

switch-dev: ## Cambia las variables de entorno a DESARROLLO
	./scripts/switch-env.sh dev

switch-prod: ## Cambia las variables de entorno a PRODUCCIÓN
	./scripts/switch-env.sh prod

# --- Utilidades ---

lint: ## Ejecuta el linter (si está configurado)
	./gradlew check

format: ## Formatea el código (si existe plugin de formato)
	@echo "Ejecutando formateo de código..."
	./gradlew help # Reemplazar con comando real si se agrega plugin de formato como Spotless

# Comandos por defecto
.DEFAULT_GOAL := help