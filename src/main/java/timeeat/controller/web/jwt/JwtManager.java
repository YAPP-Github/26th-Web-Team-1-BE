package timeeat.controller.web.jwt;

import java.time.Duration;
import java.util.Date;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtManager {

    private final JwtProperties jwtProperties;

    public JwtManager(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String issueAccessToken(long id) {
        return createToken(id, jwtProperties.getAccessTokenExpiration(), TokenType.ACCESS_TOKEN);
    }

    public String issueRefreshToken(long id) {
        return createToken(id, jwtProperties.getRefreshTokenExpiration(), TokenType.REFRESH_TOKEN);
    }

    private String createToken(long identifier, Duration expiration, TokenType tokenType) {
        Date now = new Date();
        Date expired = new Date(now.getTime() + expiration.toMillis());
        return Jwts.builder()
                .setSubject(Long.toString(identifier))
                .setIssuedAt(now)
                .setExpiration(expired)
                .claim("type", tokenType.name())
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public long resolveAccessToken(String accessToken) {
        return resolveToken(accessToken, TokenType.ACCESS_TOKEN);
    }

    public long resolveRefreshToken(String refreshToken) {
        return resolveToken(refreshToken, TokenType.REFRESH_TOKEN);
    }

    private long resolveToken(String token, TokenType tokenType) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            validateTokenType(claims, tokenType);
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException exception) {
            throw new BusinessException(BusinessErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new BusinessException(BusinessErrorCode.UNAUTHORIZED_MEMBER);
        }
    }

    private void validateTokenType(Claims claims, TokenType tokenType) {
        String extractTokenType = claims.get("type", String.class);
        if (!extractTokenType.equals(tokenType.name())) {
            throw new BusinessException(BusinessErrorCode.UNAUTHORIZED_MEMBER);
        }
    }
}
