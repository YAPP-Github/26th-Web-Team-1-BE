package timeeat.controller.web.jwt;

import java.time.Duration;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.ConfigurationProperties;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private static final int SECRET_KEY_MIN_BYTES = 32;

    private final String secretKey;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtProperties(String secretKey, Duration accessTokenExpiration, Duration refreshTokenExpiration) {
        validate(secretKey);
        validate(accessTokenExpiration);
        validate(refreshTokenExpiration);

        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    private void validate(String secretKey) {
        if (secretKey == null || secretKey.getBytes().length < SECRET_KEY_MIN_BYTES) {
            // TODO Initialize error 논의
            throw new RuntimeException("JWT secret key must be at least 32 bytes");
        }
    }

    private void validate(Duration expiration) {
        if (expiration == null) {
            throw new RuntimeException("JWT token duration cannot be null");
        }
        if (expiration.isNegative()) {
            throw new RuntimeException("JWT token duration must be positive");
        }
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
