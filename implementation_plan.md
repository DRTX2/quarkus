# Visión del Project Owner: Evolución a Nivel Senior / Staff Engineer

Hola equipo. He revisado exhaustivamente el estado actual del repositorio `learning-quarkus` y he analizado el portafolio de proyectos anteriores. 

## Análisis del Portafolio y Estado Actual
Actualmente tienes un nivel muy sólido en desarrollo Full-Stack y Microservicios. Tu portafolio demuestra dominio en:
- Arquitecturas distribuidas y orientadas a eventos (RabbitMQ, gRPC).
- Multi-tenancy avanzado (EF Core global filters, Postgres Citus Sharding).
- Diversidad de frameworks (Spring Boot, NestJS, .NET, Laravel, React, Angular).
- Patrones de diseño limpios (Hexagonal, CQRS con MediatR).

En este repositorio (`learning-quarkus`), ya tenemos una base excelente: un **Módulo de Autenticación Zero-Trust** (JWT, Roles, Auditoría, Refresh Tokens) implementado con Arquitectura Hexagonal estricta usando Quarkus, Panache y PostgreSQL.

## El Siguiente Nivel: ¿Qué nos falta explorar?
Para que tu perfil sea percibido como **Senior/Staff Engineer**, debemos alejarnos de los "sistemas de gestión" (ERPs, clínicas, inventarios) y adentrarnos en herramientas de infraestructura, resiliencia sistémica (SRE) o FinOps. Quarkus brilla por su bajo consumo de memoria y tiempos de arranque (GraalVM), lo que lo hace ideal para integrarse nativamente con Kubernetes.

## Propuesta de Proyecto: "KubeChaos AIOps - Plataforma de Ingeniería del Caos y Resiliencia Autónoma"
*(Inspirado en los proyectos #9 y #22 del documento `ideas.md`)*

Vamos a convertir este proyecto en una **Herramienta SaaS para Site Reliability Engineers (SREs)**. 

### ¿Qué hará el sistema?
1. **Control de Infraestructura (K8s Native):** El backend en Quarkus se conectará directamente a la API de Kubernetes (usando Fabric8 Kubernetes Client).
2. **Ingeniería del Caos (Chaos Engineering):** Permitirá programar "Experimentos de Caos" en clústeres reales (ej. apagar pods aleatoriamente, inyectar latencia de red, agotar CPU).
3. **Observabilidad Inteligente (AIOps):** Monitoreará las métricas (vía Prometheus) durante el caos. Usaremos **Quarkus LangChain4j** integrado con un LLM para analizar automáticamente cómo reaccionó el sistema y generar un "Reporte de Resiliencia ISO 25010" sugiriendo mejoras de código.
4. **Seguridad (Ya implementado):** Solo usuarios con rol `ROLE_SRE_ADMIN` (gestionados por el módulo de Auth que ya tienes) podrán ejecutar estos experimentos destructivos.

### ¿Por qué es un proyecto "Senior"?
- Ya has desplegado en K8s, pero ahora vas a **programar contra el kernel de K8s** construyendo casi un *Kubernetes Operator* en Java.
- Añades **Ingeniería del Caos**, una disciplina de nicho altísimamente valorada (Netflix Chaos Monkey).
- Explota el poder de **Quarkus** al máximo: compilaremos el orquestador a GraalVM (Native Image) para que consuma <50MB de RAM operando dentro del clúster.

---

## Open Questions
> [!IMPORTANT]
> **Preguntas para ti antes de empezar:**
> 1. ¿Te atrae la idea de ir hacia el área de Infraestructura/DevOps/SRE para este proyecto, o prefieres algo más orientado a procesamiento masivo de datos / FinOps?
> 2. ¿Tienes acceso a algún LLM API (OpenAI, Gemini, o local vía Ollama) para integrar la parte de AIOps usando Quarkus LangChain4j?
> 3. Podemos enfocarlo como una API Headless (solo backend para CLI) o integrar un frontend más adelante. ¿Te parece bien empezar 100% enfocados en la arquitectura core del backend?

## Proposed Changes (Fase 1)

### 1. Dominio de Experimentos de Caos (Chaos Domain)
#### [NEW] `src/main/java/com/drtx/qks/domain/model/ChaosExperiment.java`
#### [NEW] `src/main/java/com/drtx/qks/domain/ports/in/chaos/ExecuteChaosUseCase.java`

### 2. Integración con Kubernetes
#### [NEW] `build.gradle` (Modificación)
Se añadirá la dependencia de `quarkus-kubernetes-client` y `quarkus-langchain4j`.
#### [NEW] `src/main/java/com/drtx/qks/adapters/out/kubernetes/KubernetesChaosAdapter.java`
Implementará la inyección de fallos contactando la API del clúster.

### 3. Integración de IA (AIOps)
#### [NEW] `src/main/java/com/drtx/qks/adapters/out/ai/ResilienceAnalyzerAdapter.java`

## Verification Plan

### Automated Tests
- Tests unitarios del motor de evaluación usando Mockito para falsear las respuestas de la API de Kubernetes.
- TestContainers para levantar un clúster k3s efímero y probar la inyección de caos real en un pod de prueba.

### Manual Verification
- Compilar a Native Image (`./gradlew build -Dquarkus.package.type=native`).
- Desplegar en un entorno local (Minikube o Docker Desktop) y destruir un pod arbitrario mediante un cURL al endpoint de nuestro sistema protegido por JWT.
