package timeeat.domain.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoreHoursTest {

    @Nested
    @DisplayName("영업시간 생성 시")
    class CreateStoreHoursTest {

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

            assertThatThrownBy(() -> new StoreHours(null, close))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_TIME_NULL);
        }

        @Test
        void closeTime이_null이면_예외를_던진다() {
            LocalTime open = LocalTime.of(9, 0);

            assertThatThrownBy(() -> new StoreHours(open, null))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_TIME_NULL);
        }

        @Test
        void openTime이_closeTime보다_늦으면_예외를_던진다() {
            LocalTime open = LocalTime.of(20, 0);
            LocalTime close = LocalTime.of(9, 0);

            assertThatThrownBy(() -> new StoreHours(open, close))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_TIME_ORDER);
        }
    }
}