package timeeat.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterestAreaTest {

    @Nested
    @DisplayName("from 메서드 테스트")
    class FromMethodTest {

        @Test
        void 유효한_이름으로_InterestArea_객체를_반환한다() {
            assertThat(InterestArea.from("강남구")).isEqualTo(InterestArea.GANGNAM);
            assertThat(InterestArea.from("서초구")).isEqualTo(InterestArea.SEOCHO);
        }

        @Test
        void 유효하지_않은_이름으로_예외를_던진다() {
            assertThatThrownBy(() -> InterestArea.from("없는동네"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_INTEREST_AREA);
        }
    }
}
