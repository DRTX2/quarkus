# 🔐 Módulo de Autenticación - Guía Completa

## 📋 Características Implementadas

✅ **Registro de usuarios** - Endpoint `/api/auth/register`
✅ **Login con JWT** - Endpoint `/api/auth/login`
✅ **Información del usuario** - Endpoint `/api/auth/me`
✅ **Cambio de password** - Endpoint `/api/auth/change-password`
✅ **Hash de passwords con BCrypt**
✅ **Tokens JWT con RSA-2048**
✅ **Roles y permisos**
✅ **Usuarios de prueba** en base de datos

---

## 🚀 Endpoints Disponibles

### 1. Registro de Usuario

**POST** `/api/auth/register`

**Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepass123"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 3,
    "username": "johndoe",
    "email": "john@example.com",
    "roles": ["USER"],
    "enabled": true
  }
}
```

**Errores:**
- `400` - Datos inválidos (validación)
- `409` - Username o email ya existe

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

---

### 2. Login

**POST** `/api/auth/login`

**Body:**
```json
{
  "usernameOrEmail": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "roles": ["ADMIN", "USER"],
    "enabled": true
  }
}
```

**Errores:**
- `401` - Credenciales inválidas
- `401` - Usuario deshabilitado

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'
```

---

### 3. Información del Usuario Actual

**GET** `/api/auth/me`

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ADMIN", "USER"],
  "enabled": true
}
```

**Errores:**
- `401` - Token no válido o expirado

**Ejemplo con curl:**
```bash
TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4. Cambiar Password

**PUT** `/api/auth/change-password`

**Headers:**
```
Authorization: Bearer <token>
```

**Body:**
```json
{
  "currentPassword": "oldpass123",
  "newPassword": "newpass456"
}
```

**Response (200 OK):**
```json
{
  "message": "Password actualizado correctamente"
}
```

**Errores:**
- `400` - Password actual incorrecto
- `401` - Token no válido

**Ejemplo con curl:**
```bash
curl -X PUT http://localhost:8080/api/auth/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "admin123",
    "newPassword": "newadmin456"
  }'
```

---

### 5. Endpoint de Administrador (Ejemplo)

**GET** `/api/auth/admin`

**Headers:**
```
Authorization: Bearer <token>
```

**Requiere:** Rol `ADMIN`

**Response (200 OK):**
```json
{
  "message": "Acceso a endpoint de administrador concedido"
}
```

**Errores:**
- `401` - Token no válido
- `403` - Usuario sin rol ADMIN

---

## 👥 Usuarios de Prueba

La migración de base de datos crea 2 usuarios por defecto:

### Admin
- **Username:** `admin`
- **Email:** `admin@example.com`
- **Password:** `admin123`
- **Roles:** `ADMIN`, `USER`

### Usuario Normal
- **Username:** `user`
- **Email:** `user@example.com`
- **Password:** `user123`
- **Roles:** `USER`

---

## 🔒 Seguridad

### Hash de Passwords
- **Algoritmo:** BCrypt
- **Rounds:** 12 (configuración predeterminada segura)
- Los passwords **NUNCA** se almacenan en texto plano

### Tokens JWT
- **Algoritmo:** RSA-256 (asimétrico)
- **Tamaño de clave:** 2048 bits
- **Duración:** Configurable vía `JWT_DURATION` (default: 3600 segundos = 1 hora)
- **Issuer:** Configurable vía `JWT_ISSUER`

### Claims del Token
```json
{
  "iss": "https://learning-quarkus-dev",
  "upn": "admin",
  "groups": ["ADMIN", "USER"],
  "email": "admin@example.com",
  "userId": "1",
  "iat": 1674000000,
  "exp": 1674003600
}
```

---

## 🏗️ Arquitectura

### Capas Implementadas

```
┌─────────────────────────────────────────┐
│     Adapters (REST Controllers)        │
│  - AuthController                       │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│     Application (Services & DTOs)       │
│  - AuthenticationService                │
│  - TokenService                         │
│  - DTOs (RegisterRequest, etc.)         │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│     Domain (Models & Ports)             │
│  - User (model)                         │
│  - AuthenticationUseCase (port)         │
│  - TokenGenerationUseCase (port)        │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│     Infrastructure (Persistence)        │
│  - UserRepositoryAdapter                │
│  - UserEntity                           │
│  - UserPanacheRepository                │
└─────────────────────────────────────────┘
```

### Archivos Creados

```
src/main/java/com/drtx/qks/
├── adapters/in/rest/auth/
│   └── AuthController.java                    ← Endpoints REST
├── application/
│   ├── dtos/auth/
│   │   ├── AuthResponse.java                 ← Response con token
│   │   ├── RegisterRequest.java              ← DTO de registro
│   │   ├── LoginRequest.java                 ← DTO de login
│   │   ├── ChangePasswordRequest.java        ← DTO cambio password
│   │   └── UserInfo.java                     ← Info del usuario
│   ├── mappers/
│   │   └── UserInfoMapper.java               ← Mapper User -> UserInfo
│   └── services/auth/
│       ├── AuthenticationService.java        ← Lógica de autenticación
│       └── TokenService.java                 ← Generación de JWT
└── domain/ports/in/auth/
    ├── AuthenticationUseCase.java            ← Puerto de autenticación
    └── TokenGenerationUseCase.java           ← Puerto de tokens

src/main/resources/db/migration/
└── V2__Create_users_table.sql                ← Migración de usuarios
```

---

## 🧪 Pruebas con Swagger UI

1. Abre Swagger UI: http://localhost:8080/swagger-ui

2. Prueba el registro:
   - Expande `POST /api/auth/register`
   - Click en "Try it out"
   - Ingresa datos de prueba
   - Click en "Execute"

3. Prueba el login:
   - Expande `POST /api/auth/login`
   - Usa username `admin` y password `admin123`
   - Copia el token de la respuesta

4. Autoriza Swagger:
   - Click en el botón "Authorize" (🔒) arriba a la derecha
   - Ingresa: `Bearer <tu_token>`
   - Click en "Authorize"

5. Prueba endpoints protegidos:
   - `GET /api/auth/me`
   - `PUT /api/auth/change-password`
   - `GET /api/auth/admin` (solo admin)

---

## 📝 Validaciones

### RegisterRequest
- `username`: Requerido, 3-50 caracteres
- `email`: Requerido, formato email válido
- `password`: Requerido, mínimo 6 caracteres

### LoginRequest
- `usernameOrEmail`: Requerido
- `password`: Requerido

### ChangePasswordRequest
- `currentPassword`: Mínimo 6 caracteres
- `newPassword`: Mínimo 6 caracteres

---

## 🔧 Configuración

### Variables de Entorno (.env)

```bash
# JWT
JWT_ENABLED=true
JWT_ISSUER=https://learning-quarkus-dev
JWT_DURATION=3600

# Las claves ya están generadas en:
# - src/main/resources/publicKey.pem
# - src/main/resources/privateKey.pem
```

---

## 🚀 Flujo Completo de Uso

### 1. Registro de un nuevo usuario
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "mypassword123"
  }' | jq .
```

### 2. Guardar el token
```bash
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "newuser",
    "password": "mypassword123"
  }' | jq -r '.token')

echo "Token: $TOKEN"
```

### 3. Acceder a endpoints protegidos
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### 4. Cambiar password
```bash
curl -X PUT http://localhost:8080/api/auth/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "mypassword123",
    "newPassword": "newsecurepass456"
  }' | jq .
```

---

## 🛡️ Proteger tus Endpoints

### Ejemplo 1: Endpoint público
```java
@GET
@Path("/public")
@PermitAll
public Response publicEndpoint() {
    return Response.ok("Accesible para todos").build();
}
```

### Ejemplo 2: Endpoint para usuarios autenticados
```java
@GET
@Path("/protected")
@RolesAllowed({"USER", "ADMIN"})
@SecurityRequirement(name = "bearer-jwt")
public Response protectedEndpoint() {
    return Response.ok("Solo usuarios autenticados").build();
}
```

### Ejemplo 3: Endpoint solo para admin
```java
@DELETE
@Path("/users/{id}")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "bearer-jwt")
public Response deleteUser(@PathParam("id") Long id) {
    // Solo admins pueden eliminar usuarios
    return Response.noContent().build();
}
```

### Ejemplo 4: Obtener información del usuario actual
```java
@Inject
JsonWebToken jwt;

@GET
@Path("/profile")
@RolesAllowed({"USER", "ADMIN"})
public Response getProfile() {
    String username = jwt.getName();
    String email = jwt.getClaim("email");
    Long userId = Long.parseLong(jwt.getClaim("userId"));
    Set<String> roles = jwt.getGroups();
    
    return Response.ok(Map.of(
        "username", username,
        "email", email,
        "userId", userId,
        "roles", roles
    )).build();
}
```

---

## 📊 Base de Datos

### Tabla `users`
```sql
Column         | Type          | Nullable
---------------|---------------|----------
id             | BIGSERIAL     | NOT NULL (PK)
username       | VARCHAR(50)   | NOT NULL (UNIQUE)
email          | VARCHAR(255)  | NOT NULL (UNIQUE)
password_hash  | VARCHAR(255)  | NOT NULL
enabled        | BOOLEAN       | NOT NULL (DEFAULT true)
created_at     | TIMESTAMP     | NOT NULL
updated_at     | TIMESTAMP     | NOT NULL
```

### Tabla `user_roles`
```sql
Column    | Type         | Nullable
----------|--------------|----------
user_id   | BIGINT       | NOT NULL (FK -> users.id)
role      | VARCHAR(50)  | NOT NULL
          | PRIMARY KEY (user_id, role)
```

---

## 🎯 Próximos Pasos

### Mejoras Opcionales

1. **Refresh Tokens**
   - Implementar tokens de larga duración
   - Endpoint `/api/auth/refresh`

2. **Verificación de Email**
   - Enviar email con token de verificación
   - Endpoint `/api/auth/verify-email`

3. **Recuperación de Password**
   - Endpoint `/api/auth/forgot-password`
   - Endpoint `/api/auth/reset-password`

4. **Rate Limiting**
   - Limitar intentos de login
   - Prevenir ataques de fuerza bruta

5. **Auditoría**
   - Registrar intentos de login
   - Log de cambios de password

6. **Multi-Factor Authentication (MFA)**
   - TOTP (Google Authenticator)
   - SMS o email

---

## ✅ Checklist

- [x] Registro de usuarios
- [x] Login con JWT
- [x] Hash de passwords con BCrypt
- [x] Validaciones de entrada
- [x] Manejo de errores
- [x] Roles y permisos
- [x] Endpoints protegidos
- [x] Migración de base de datos
- [x] Usuarios de prueba
- [x] Documentación OpenAPI

---

**¡El módulo de autenticación está completo y listo para usar!** 🎉

Para probar, ejecuta:
```bash
./gradlew quarkusDev
```

Y abre: http://localhost:8080/swagger-ui

