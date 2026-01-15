# 🔐 Guía de Seguridad JWT en Quarkus

## 📋 Configuración Implementada

### ✅ Componentes Creados

1. **JwtService** - Servicio para generar tokens JWT
2. **AuthResource** - Endpoints de autenticación (/api/auth/*)
3. **LoginRequest/LoginResponse** - DTOs para login
4. **Claves RSA** - privateKey.pem y publicKey.pem

## 🚀 Cómo Usar JWT

### 1. Obtener un Token (Login)

```bash
# Admin user
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin"
  }'

# Response:
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600
}
```

```bash
# Regular user
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "user"
  }'
```

### 2. Usar el Token en Requests

```bash
# Obtener información del usuario actual
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <TU_TOKEN_AQUI>"

# Acceder a endpoint protegido de admin
curl -X GET http://localhost:8080/api/auth/admin \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

## 🔧 Proteger tus Endpoints

### Usando Anotaciones de Seguridad

```java
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

@Path("/api/productos")
public class ProductoResource {

    // Público - sin autenticación
    @GET
    @PermitAll
    public List<Producto> listar() {
        return productoService.findAll();
    }

    // Requiere autenticación (cualquier rol)
    @POST
    @RolesAllowed({"user", "admin"})
    public Response crear(Producto producto) {
        return Response.ok(productoService.save(producto)).build();
    }

    // Solo administradores
    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response eliminar(@PathParam("id") Long id) {
        productoService.delete(id);
        return Response.noContent().build();
    }
}
```

### Acceder a Información del Token

```java
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.inject.Inject;

@Path("/api/profile")
public class ProfileResource {

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"user", "admin"})
    public Response getProfile() {
        String username = jwt.getName();
        Set<String> roles = jwt.getGroups();
        String email = jwt.getClaim("email");
        String userId = jwt.getClaim("userId");

        return Response.ok(Map.of(
            "username", username,
            "roles", roles,
            "email", email,
            "userId", userId
        )).build();
    }
}
```

## 🎯 Estructura del Token JWT

El token generado incluye los siguientes claims:

```json
{
  "iss": "https://learning-quarkus-dev",
  "upn": "admin",
  "groups": ["admin", "user"],
  "email": "admin@example.com",
  "userId": "1",
  "iat": 1674000000,
  "exp": 1674003600
}
```

- **iss** (issuer): Emisor del token
- **upn** (user principal name): Nombre de usuario
- **groups**: Roles del usuario
- **email**: Email del usuario (custom claim)
- **userId**: ID del usuario (custom claim)
- **iat**: Timestamp de emisión
- **exp**: Timestamp de expiración

## 🔑 Generar Nuevas Claves RSA

Si necesitas generar nuevas claves en producción:

```bash
# Generar clave privada
openssl genrsa -out privateKey.pem 2048

# Generar clave pública desde la privada
openssl rsa -in privateKey.pem -pubout -out publicKey.pem

# Mover a recursos
mv privateKey.pem src/main/resources/
mv publicKey.pem src/main/resources/
```

⚠️ **IMPORTANTE**: En producción:
- NO subir `privateKey.pem` a Git
- Usar gestores de secretos (AWS Secrets Manager, etc.)
- Rotar claves regularmente

## 🛡️ Configuración de Seguridad

### Variables de Entorno (.env)

```bash
# Habilitar/deshabilitar JWT
JWT_ENABLED=true

# Secret (no usado con RSA, pero necesario para otros métodos)
JWT_SECRET=tu-secret-aqui-min-256-bits

# Issuer (quien emite el token)
JWT_ISSUER=https://learning-quarkus-dev

# Duración en segundos (1 hora = 3600)
JWT_DURATION=3600
```

### Application.yml

```yaml
quarkus:
  smallrye-jwt:
    enabled: ${JWT_ENABLED:true}

mp:
  jwt:
    verify:
      publickey:
        location: publicKey.pem
      issuer: ${JWT_ISSUER:https://learning-quarkus}
```

## 📝 Implementar en tu API

### Paso 1: Crear Entidad de Usuario

```java
@Entity
@Table(name = "users")
public class User extends PanacheEntity {
    public String username;
    public String email;
    public String passwordHash;
    
    @ElementCollection(fetch = FetchType.EAGER)
    public Set<String> roles;
}
```

### Paso 2: Crear Servicio de Autenticación

```java
@ApplicationScoped
public class AuthService {

    @Inject
    JwtService jwtService;

    public String authenticate(String username, String password) {
        User user = User.find("username", username).firstResult();
        
        if (user == null || !BCrypt.checkpw(password, user.passwordHash)) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        return jwtService.generateTokenWithClaims(
            user.username,
            user.roles,
            user.email,
            user.id.toString()
        );
    }
}
```

### Paso 3: Actualizar AuthResource

```java
@POST
@Path("/login")
@PermitAll
public Response login(LoginRequest request) {
    try {
        String token = authService.authenticate(
            request.username, 
            request.password
        );
        return Response.ok(new LoginResponse(token, tokenDuration)).build();
    } catch (UnauthorizedException e) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorMessage("Credenciales inválidas"))
                .build();
    }
}
```

## 🧪 Testing con JWT

### Test con RestAssured

```java
@QuarkusTest
public class SecureResourceTest {

    String token;

    @BeforeEach
    public void getToken() {
        token = given()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("admin", "admin"))
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("token");
    }

    @Test
    public void testSecureEndpoint() {
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/auth/me")
            .then()
            .statusCode(200)
            .body("username", is("admin"));
    }

    @Test
    public void testWithoutToken() {
        given()
            .when()
            .get("/api/auth/me")
            .then()
            .statusCode(401);
    }
}
```

## 🔍 Debugging JWT

### Ver contenido del token

Usa [jwt.io](https://jwt.io) para decodificar y verificar tokens.

### Logs de autenticación

```yaml
quarkus:
  log:
    category:
      "io.quarkus.smallrye.jwt":
        level: DEBUG
      "io.smallrye.jwt":
        level: DEBUG
```

## 🚨 Mejores Prácticas de Seguridad

1. **Nunca guardes el token en localStorage** (vulnerable a XSS)
   - Usa cookies HttpOnly
   - O sessionStorage para SPA

2. **Implementa refresh tokens**
   - Token de acceso: 15-60 minutos
   - Refresh token: 7-30 días

3. **Valida el issuer**
   - Verifica que coincida con tu servidor

4. **Implementa revocación de tokens**
   - Lista negra en Redis
   - Tokens de sesión en BD

5. **Rate limiting en login**
   - Previene ataques de fuerza bruta

6. **HTTPS obligatorio en producción**
   - Tokens viajan en headers

7. **Hashea passwords con BCrypt**
   ```bash
   implementation 'org.mindrot:jbcrypt:0.4'
   ```

## 📚 Referencias

- [Quarkus Security JWT](https://quarkus.io/guides/security-jwt)
- [MicroProfile JWT](https://github.com/eclipse/microprofile-jwt-auth)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

## 🆘 Problemas Comunes

### Token no válido
```
io.quarkus.security.UnauthorizedException: SRJWT00000
```
**Solución**: Verifica que el issuer coincida en el token y la configuración.

### Clave pública no encontrada
```
RuntimeException: No se encontró el archivo publicKey.pem
```
**Solución**: Asegúrate que `publicKey.pem` esté en `src/main/resources/`.

### Claims no accesibles
```java
String email = jwt.getClaim("email"); // null
```
**Solución**: Asegúrate de agregar el claim al generar el token.

