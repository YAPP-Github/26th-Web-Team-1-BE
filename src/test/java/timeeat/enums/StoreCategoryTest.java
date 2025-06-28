package timeeat.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

class StoreCategoryTest {

    @Nested
    @DisplayName("from 메서드 테스트")
    class FromMethod {

        @Test
        void 유효한_이름으로_StoreCategory_객체를_반환한다() {
            assertThat(StoreCategory.from("한식")).isEqualTo(StoreCategory.KOREAN);
            assertThat(StoreCategory.from("양식")).isEqualTo(StoreCategory.WESTERN);
        }

        @Test
        void 유효하지_않은_이름으로_예외를_던진다() {
            BusinessException exception = assertThrows(BusinessException.class, () -> StoreCategory.from("없는카테고리"));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_STORE_CATEGORY);
        }
    }
}
