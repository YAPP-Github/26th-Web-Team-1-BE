package timeeat.domain.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

class DiscountTest {

    private Price originalPrice;

    @BeforeEach
    void setUp() {
        originalPrice = new Price(10000);
    }

    @Nested
    @DisplayName("할인을 생성할 때")
    class CreateDiscount {

        @Test
        void 모든_정보가_정상적일_때_생성한다() {
            Integer discountPrice = 8000;
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.plusHours(2);

            Discount discount = new Discount(originalPrice, discountPrice, startTime, endTime);

            assertThat(discount.getDiscountPrice()).isEqualTo(discountPrice);
            assertThat(discount.getStartTime()).isEqualTo(startTime);
            assertThat(discount.getEndTime()).isEqualTo(endTime);
        }

        @Test
        void 할인_정보가_없어도_생성한다() {
            Discount discount = new Discount(originalPrice, null, null, null);

            assertThat(discount.getDiscountPrice()).isNull();
            assertThat(discount.getStartTime()).isNull();
            assertThat(discount.getEndTime()).isNull();
        }

        @Test
        void 할인_가격이_원가보다_비싸면_예외를_던진다() {
            Integer invalidDiscountPrice = 12000;

            BusinessException exception = assertThrows(BusinessException.class, () -> new Discount(originalPrice, invalidDiscountPrice, null, null));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_MENU_DISCOUNT_PRICE);
        }

        @Test
        void 할인_가격이_원가와_같으면_예외를_던진다() {
            Integer sameDiscountPrice = 10000;

            BusinessException exception = assertThrows(BusinessException.class, () -> new Discount(originalPrice, sameDiscountPrice, null, null));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_MENU_DISCOUNT_PRICE);
        }

        @Test
        void 할인_가격이_0원이면_예외를_던진다() {
            Integer zeroDiscountPrice = 0;

            BusinessException exception = assertThrows(BusinessException.class, () -> new Discount(originalPrice, zeroDiscountPrice, null, null));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_MENU_DISCOUNT_PRICE);
        }

        @Test
        void 시작_시간이_종료_시간보다_늦으면_예외를_던진다() {
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.minusHours(2);

            BusinessException exception = assertThrows(BusinessException.class, () -> new Discount(originalPrice, 8000, startTime, endTime));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_MENU_DISCOUNT_TIME);
        }
    }
}
