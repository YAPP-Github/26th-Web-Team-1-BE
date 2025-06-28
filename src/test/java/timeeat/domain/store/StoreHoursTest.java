package timeeat.domain.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

class StoreHoursTest {

    @Nested
    @DisplayName("영업시간 생성 시")
    class CreateStoreHours {

        @Test
        void 정상적인_영업시간으로_생성한다() {
            LocalTime open = LocalTime.of(9, 0);
            LocalTime close = LocalTime.of(18, 0);

            StoreHours storeHours = new StoreHours(open, close);

            assertThat(storeHours.getOpenTime()).isEqualTo(open);
            assertThat(storeHours.getCloseTime()).isEqualTo(close);
        }

        @Test
        void openTime이_null이면_예외를_던진다() {
            LocalTime close = LocalTime.of(18, 0);

            BusinessException exception = assertThrows(BusinessException.class, () -> new StoreHours(null, close));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_STORE_TIME_NULL);
        }

        @Test
        void closeTime이_null이면_예외를_던진다() {
            LocalTime open = LocalTime.of(9, 0);

            BusinessException exception = assertThrows(BusinessException.class, () -> new StoreHours(open, null));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_STORE_TIME_NULL);
        }

        @Test
        void openTime이_closeTime보다_늦으면_예외를_던진다() {
            LocalTime open = LocalTime.of(20, 0);
            LocalTime close = LocalTime.of(9, 0);

            BusinessException exception = assertThrows(BusinessException.class, () -> new StoreHours(open, close));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_STORE_TIME_ORDER);
        }
    }
}
