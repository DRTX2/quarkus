package com.drtx.qks.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;

/**
 * Servicio para generar tokens JWT
 */
@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "JWT_DURATION", defaultValue = "3600")
    Long tokenDuration;

    /**
     * Genera un token JWT para un usuario
     *
     * @param username Nombre de usuario
     * @param roles Roles del usuario
     * @return Token JWT firmado
     */
    public String generateToken(String username, Set<String> roles) {
        try {
            return Jwt.issuer(issuer)
                    .upn(username)
                    .groups(roles)
                    .expiresIn(Duration.ofSeconds(tokenDuration))
                    .sign(loadPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException("Error generando token JWT", e);
        }
    }

    /**
     * Genera un token JWT con claims personalizados
     *
     * @param username Nombre de usuario
     * @param roles Roles del usuario
     * @param email Email del usuario
     * @param userId ID del usuario
     * @return Token JWT firmado
     */
    public String generateTokenWithClaims(String username, Set<String> roles, String email, String userId) {
        try {
            return Jwt.issuer(issuer)
                    .upn(username)
                    .groups(roles)
                    .claim("email", email)
                    .claim("userId", userId)
                    .expiresIn(Duration.ofSeconds(tokenDuration))
                    .sign(loadPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException("Error generando token JWT", e);
        }
    }

    /**
     * Carga la clave privada desde el archivo privateKey.pem
     */
    private PrivateKey loadPrivateKey() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/privateKey.pem")) {
            if (is == null) {
                throw new RuntimeException("No se encontró el archivo privateKey.pem");
            }

            String key = new String(is.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        }
    }
}

