package timeeat.domain.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuTest {

    @Nested
    @DisplayName("메뉴를 생성할 때")
    class CreateMenuTest {

        @Test
        void 모든_정보가_정상적일_때_메뉴를_생성한다() {
            String name = "해물 볶음밥";
            Integer price = 15000;
            Integer discountPrice = 12000;

            Menu menu = new Menu(name, "설명", price, "url", discountPrice, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

            assertAll(
                    () -> assertThat(menu.getName()).isEqualTo(name),
                    () -> assertThat(menu.getPrice().getValue()).isEqualTo(price),
                    () -> assertThat(menu.getDiscount().getDiscountPrice()).isEqualTo(discountPrice)
            );
        }

        @Test
        void 이름이_null이면_예외를_던진다() {
            assertThatThrownBy(() -> new Menu(null, "설명", 10000, "url", null, null, null))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_MENU_NAME);
        }

        @Test
        void 할인_가격이_원가보다_비싸면_예외를_던진다() {
            Integer price = 10000;
            Integer invalidDiscountPrice = 12000;

            assertThatThrownBy(() -> new Menu("이름", "설명", price, "url", invalidDiscountPrice, null, null))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_MENU_DISCOUNT_PRICE);
        }
    }
}
