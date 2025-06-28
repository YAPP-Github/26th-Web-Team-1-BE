package timeeat.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

class InterestAreaTest {

    @Nested
    @DisplayName("from 메서드 테스트")
    class FromMethod {

        @Test
        void 유효한_이름으로_InterestArea_객체를_반환한다() {
            assertThat(InterestArea.from("강남구")).isEqualTo(InterestArea.GANGNAM);
            assertThat(InterestArea.from("서초구")).isEqualTo(InterestArea.SEOCHO);
        }

        @Test
        void 유효하지_않은_이름으로_예외를_던진다() {
            BusinessException exception = assertThrows(BusinessException.class, () -> InterestArea.from("없는동네"));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_INTEREST_AREA);
        }
    }
}
