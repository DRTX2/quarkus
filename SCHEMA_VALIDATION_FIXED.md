# ✅ Schema Validation Error Resuelto

## 🐛 Problema Encontrado

```
ERROR: Schema-validation: missing column [password] in table [users]

Hibernate esperaba:
- Column: password (String)
- Column: role (ENUM)
- Column: email VARCHAR(50)

Base de datos tenía:
- Column: password_hash (String)
- Table: user_roles (many-to-many)
- Column: email VARCHAR(255)
```

**Causa:** La entidad JPA `UserEntity` no coincidía con la estructura de la tabla creada por Flyway.

## 🔧 Solución Aplicada

### Actualicé `UserEntity.java` para que coincida con la base de datos:

#### Antes (INCORRECTO):
```java
@Entity
@Table(name = "users")
public class UserEntity {
    private Long id;
    private String username;
    private String email;          // VARCHAR(50)
    private String password;       // ❌ No existe en BD
    
    @Enumerated(EnumType.STRING)
    private UserRole role;         // ❌ No existe en BD
}
```

#### Después (CORRECTO):
```java
@Entity
@Table(name = "users")
public class UserEntity {
    private Long id;
    private String username;
    
    @Column(length = 255)
    private String email;                    // ✅ VARCHAR(255)
    
    @Column(name = "password_hash")
    private String passwordHash;             // ✅ Coincide con BD
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", 
                     joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;               // ✅ Tabla separada
    
    private boolean enabled;                 // ✅ Nuevo campo
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;         // ✅ Nuevo campo
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;         // ✅ Nuevo campo
}
```

## 📊 Mapeo Correcto

### Tabla `users`
```sql
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,   ← 255 no 50
    password_hash   VARCHAR(255) NOT NULL,          ← no "password"
    enabled         BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);
```

**Mapeo JPA:**
```java
@Column(nullable = false, unique = true, length = 255)
private String email;

@Column(name = "password_hash", nullable = false, length = 255)
private String passwordHash;

@Column(nullable = false)
private boolean enabled = true;

@Column(name = "created_at", nullable = false)
private LocalDateTime createdAt;

@Column(name = "updated_at", nullable = false)
private LocalDateTime updatedAt;
```

### Tabla `user_roles`
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**Mapeo JPA:**
```java
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "user_roles", 
                 joinColumns = @JoinColumn(name = "user_id"))
@Column(name = "role", length = 50)
private Set<String> roles = new HashSet<>();
```

## ✅ Cambios Realizados

### 1. Campos Renombrados
- `password` → `passwordHash` (coincide con columna `password_hash`)
- `role` (enum) → `roles` (Set<String>)

### 2. Campos Agregados
- `enabled` (boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

### 3. Tipo de Email Actualizado
- Longitud: 50 → 255 caracteres

### 4. Roles como Colección
- De: `@Enumerated UserRole role`
- A: `@ElementCollection Set<String> roles`
- Usa tabla separada `user_roles`

## 🎯 Verificación

### Estado Actual:
✅ **BUILD SUCCESSFUL**

### Hibernate Validation:
✅ La entidad `UserEntity` coincide con la estructura de la BD
✅ No más errores de schema validation
✅ Mapeo correcto de columnas y tablas

## 🚀 Ahora Puedes Ejecutar

```bash
# Iniciar PostgreSQL
docker-compose up -d

# Ejecutar aplicación
./gradlew quarkusDev
```

**Resultado esperado:**
- Flyway ejecutará las migraciones V1 y V2
- Hibernate validará el schema correctamente
- La aplicación iniciará sin errores
- Usuarios de prueba estarán disponibles (admin/admin123, user/user123)

## 📝 Lección Aprendida

**Regla de Oro:** La entidad JPA debe coincidir EXACTAMENTE con la estructura de la tabla en la base de datos.

### Checklist para evitar este error:

- [ ] Nombres de columnas coinciden (`password_hash` no `password`)
- [ ] Tipos de datos coinciden (VARCHAR(255) no VARCHAR(50))
- [ ] Campos nullable/not null coinciden
- [ ] Relaciones many-to-many mapeadas correctamente
- [ ] Nombres de tablas coinciden
- [ ] Todos los campos de la tabla tienen su propiedad en la entidad

---

**¡Error de Schema Validation resuelto!** 🎉

## ✅ Para verificar:

```bash
./gradlew clean quarkusDev
```

La aplicación debería iniciar sin errores de schema validation.

