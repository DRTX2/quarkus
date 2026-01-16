package com.drtx.qks.application.services.auth;
import com.drtx.qks.domain.ports.in.auth.TokenGenerationUseCase;
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
@ApplicationScoped
public class TokenService implements TokenGenerationUseCase {
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;
    @ConfigProperty(name = "JWT_DURATION", defaultValue = "3600")
    Long tokenDuration;
    @Override
    public String generateToken(Long userId, String username, String email, Set<String> roles) {
        try {
            return Jwt.issuer(issuer)
                    .upn(username)
                    .groups(roles)
                    .claim("email", email)
                    .claim("userId", userId.toString())
                    .expiresIn(Duration.ofSeconds(tokenDuration))
                    .sign(loadPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException("Error generando token JWT", e);
        }
    }
    @Override
    public Long getTokenDuration() {
        return tokenDuration;
    }
    private PrivateKey loadPrivateKey() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/privateKey.pem")) {
            if (is == null) {
                throw new RuntimeException("No se encontro el archivo privateKey.pem");
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

