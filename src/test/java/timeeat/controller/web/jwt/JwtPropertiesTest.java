package timeeat.controller.web.jwt;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class JwtPropertiesTest {

    @Nested
    class ValidateSecretKey {

        @ParameterizedTest
        @NullAndEmptySource
        void 비밀키가_비어있으면_예외를_발생시킨다(String secretKey) {

            assertThatThrownBy(() -> new JwtProperties(secretKey, Duration.ofMinutes(30), Duration.ofDays(14)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("JWT secret key must be at least 32 bytes");
        }

        @Test
        void 비밀키가_특정_길이보다_짧으면_예외를_발생시킨다() {
            String secretKey = "1".repeat(31); // 31 bytes

            assertThatThrownBy(() -> new JwtProperties(secretKey, Duration.ofMinutes(30), Duration.ofDays(14)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("JWT secret key must be at least 32 bytes");
        }

        @Test
        void 비밀키가_특정_길이보다_길면_정상적으로_생성된다() {
            String secretKey = "1".repeat(32); // 32 bytes

            assertThatCode(() -> new JwtProperties(secretKey, Duration.ofMinutes(30), Duration.ofDays(14)))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class ValidateExpiration {

        private final String secretKey = "validSecretKey".repeat(8);

        @Test
        void 만료기간이_비어있으면_예외를_발생시킨다() {
            assertThatThrownBy(() -> new JwtProperties(secretKey, null, Duration.ofDays(14)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("JWT token duration cannot be null");
        }

        @Test
        void 만료기간이_음수이면_예외를_발생시킨다() {
            assertThatThrownBy(() -> new JwtProperties(secretKey, Duration.ofHours(1), Duration.ofSeconds(-1)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("JWT token duration must be positive");
        }
    }
}
