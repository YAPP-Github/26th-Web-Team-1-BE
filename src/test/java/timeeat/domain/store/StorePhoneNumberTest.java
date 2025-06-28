package timeeat.domain.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

class StorePhoneNumberTest {

    @Nested
    @DisplayName("가게 번호를 생성할 때")
    class CreateStorePhoneNumber {

        @ParameterizedTest
        @ValueSource(strings = {"021234567", "03112345678", "15771234", "077012345678"})
        void 가게_번호를_정상적으로_생성한다(String validPhoneNumber) {
            StorePhoneNumber phoneNumber = new StorePhoneNumber(validPhoneNumber);

            assertThat(phoneNumber.getValue()).isEqualTo(validPhoneNumber);
        }

        @Test
        void null_값으로_생성할_수_있다() {
            String nullNumber = null;

            StorePhoneNumber phoneNumber = new StorePhoneNumber(nullNumber);

            assertThat(phoneNumber.getValue()).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"1234567", "1234567890123", "02-1234-5678", "abcdefghij"})
        void 번호_형식이_올바르지_않으면_예외를_던진다(String invalidNumber) {
            BusinessException exception = assertThrows(BusinessException.class, () -> new StorePhoneNumber(invalidNumber));
            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_STORE_PHONE_NUMBER);
        }
    }
}
