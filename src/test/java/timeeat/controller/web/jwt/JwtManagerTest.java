package timeeat.controller.web.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

class JwtManagerTest {

    private final String secretKey = "secretKey".repeat(32);
    private final JwtManager jwtManager = new JwtManager(
            new JwtProperties(secretKey, Duration.ofHours(1), Duration.ofDays(14)));

    @Nested
    class IssueAccessToken {

        @Test
        void 액세스_토큰을_발행할_수_있다() {
            long id = 12345L;

            assertThatCode(() -> jwtManager.issueAccessToken(id))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class IssueRefreshToken {

        @Test
        void 리프레시_토큰을_발행할_수_있다() {
            long id = 12345L;

            assertThatCode(() -> jwtManager.issueRefreshToken(id))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class ResolveAccessToken {

        @Test
        void 액세스_토큰을_해석할_수_있다() {
            long id = 12345L;
            String accessToken = jwtManager.issueAccessToken(id);

            long actualId = jwtManager.resolveAccessToken(accessToken);

            assertThat(actualId).isEqualTo(id);
        }

        @Test
        void 만료된_액세스_토큰을_해석하면_에러가_발생한다() {
            Duration accessTokenExpiration = Duration.ZERO;
            JwtManager jwtManager = new JwtManager(
                    new JwtProperties(secretKey, accessTokenExpiration, Duration.ofDays(14)));
            long id = 12345L;
            String accessToken = jwtManager.issueAccessToken(id);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> jwtManager.resolveAccessToken(accessToken));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.EXPIRED_TOKEN);
        }

        @Test
        void 유효하지_않은_액세스_토큰을_해석하면_에러가_발생한다() {
            String accessToken = "aaa.bbb.ccc";

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> jwtManager.resolveAccessToken(accessToken));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.UNAUTHORIZED_MEMBER);
        }

        @Test
        void 액세스_토큰의_타입이_다르면_에러가_발생한다() {
            long id = 12345L;
            String refreshToken = jwtManager.issueRefreshToken(id);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> jwtManager.resolveAccessToken(refreshToken));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.UNAUTHORIZED_MEMBER);
        }
    }

    @Nested
    class ResolveRefreshToken {

        @Test
        void 리프레시_토큰을_해석할_수_있다() {
            long id = 12345L;
            String refreshToken = jwtManager.issueRefreshToken(id);

            long actualId = jwtManager.resolveRefreshToken(refreshToken);

            assertThat(actualId).isEqualTo(id);
        }

        @Test
        void 만료된_리프레시_토큰을_해석하면_에러가_발생한다() {
            Duration refreshTokenExpiration = Duration.ZERO;
            JwtManager jwtManager = new JwtManager(
                    new JwtProperties(secretKey, Duration.ofHours(1), refreshTokenExpiration));
            long id = 12345L;
            String refreshToken = jwtManager.issueRefreshToken(id);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> jwtManager.resolveRefreshToken(refreshToken));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.EXPIRED_TOKEN);
        }

        @Test
        void 유효하지_않은_리프레시_토큰을_해석하면_에러가_발생한다() {
            String refreshToken = "aaa.bbb.ccc";

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> jwtManager.resolveRefreshToken(refreshToken));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.UNAUTHORIZED_MEMBER);
        }

        @Test
        void 리프레시_토큰의_타입이_다르면_에러가_발생한다() {
            long id = 12345L;
            String accessToken = jwtManager.issueAccessToken(id);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> jwtManager.resolveRefreshToken(accessToken));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.UNAUTHORIZED_MEMBER);
        }
    }
}
