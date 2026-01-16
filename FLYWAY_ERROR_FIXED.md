# ✅ Error de Flyway Resuelto

## 🐛 Problema Encontrado

```
FlywayException: Found more than one migration with version 2
Offenders:
-> V2__Create_users_table.sql (SQL)
-> V2__create_user_table.sql (SQL)
```

**Causa:** Existían 2 archivos de migración con la misma versión V2, lo cual Flyway no permite.

## 🔧 Solución Aplicada

### 1. Identificar los Archivos Duplicados

```bash
# Archivo 1 (antiguo - INCOMPATIBLE)
V2__create_user_table.sql
- Estructura simple sin campos necesarios
- No tiene password_hash
- No tiene roles separados
- No tiene enabled, created_at, updated_at

# Archivo 2 (nuevo - CORRECTO)
V2__Create_users_table.sql
- Estructura completa con todos los campos
- password_hash para BCrypt
- Tabla user_roles (many-to-many)
- enabled, created_at, updated_at
- Índices para performance
- Usuarios de prueba pre-cargados
```

### 2. Eliminar el Archivo Antiguo

```bash
rm src/main/resources/db/migration/V2__create_user_table.sql
```

### 3. Limpiar Build

```bash
./gradlew clean
```

Esto elimina los archivos compilados antiguos del directorio `build/resources/main/db/migration/`.

## ✅ Estado Actual

**Migraciones en el proyecto:**
- ✅ `V1__Initial_schema.sql` - Schema inicial
- ✅ `V2__Create_users_table.sql` - Tabla de usuarios (ÚNICA)

## 📊 Estructura de la Migración V2

### Tabla `users`
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Tabla `user_roles`
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Datos Pre-cargados

**Admin:**
- Username: `admin`
- Email: `admin@example.com`
- Password: `admin123`
- Roles: ADMIN, USER

**User:**
- Username: `user`
- Email: `user@example.com`
- Password: `user123`
- Roles: USER

## 🚀 Ahora Puedes Ejecutar

```bash
# Ejecutar la aplicación
./gradlew quarkusDev

# Flyway ejecutará automáticamente las migraciones
# V1 -> Schema inicial
# V2 -> Usuarios y roles
```

## 📝 Reglas de Flyway

1. **Versiones únicas:** Cada migración debe tener una versión única
2. **Nomenclatura:** `V{version}__{description}.sql`
3. **Orden:** Las versiones se ejecutan en orden numérico
4. **Inmutabilidad:** Una vez ejecutada, una migración no debe modificarse

### ✅ Correcto
```
V1__Initial_schema.sql
V2__Create_users_table.sql
V3__Add_products_table.sql
```

### ❌ Incorrecto
```
V1__Initial_schema.sql
V2__Create_users_table.sql
V2__create_user_table.sql  ← Error: versión duplicada
```

## 🎯 Próximas Migraciones

Si necesitas agregar más tablas o cambios:

```sql
-- V3__Add_products_table.sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- V4__Add_orders_table.sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

**¡Error de Flyway resuelto! Ahora puedes iniciar la aplicación.** 🎉

## ✅ Comando para iniciar:

```bash
./gradlew quarkusDev
```

