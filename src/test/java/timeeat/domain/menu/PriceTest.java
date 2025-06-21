package timeeat.domain.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Nested
    @DisplayName("가격을 생성할 때")
    class CreatePriceTest {

        @Test
        void 정상적인_가격으로_생성한다() {
            Integer validPriceValue = 10000;

            Price price = new Price(validPriceValue);

            assertThat(price.getValue()).isEqualTo(validPriceValue);
        }

        @Test
        void 가격이_null이면_예외를_던진다() {
            assertThatThrownBy(() -> new Price(null))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_MENU_PRICE);
        }

        @Test
        void 가격이_0원_이하이면_예외를_던진다() {
            assertThatThrownBy(() -> new Price(0))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_MENU_PRICE);
        }
    }
}
