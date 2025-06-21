package timeeat.domain.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.enums.StoreCategory;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class StoreTest {

    @Nested
    @DisplayName("가게 정보를 생성할 때")
    class CreateStoreTest {

        @Test
        void 모든_정보가_정상적일_때_가게를_생성한다() {
            String name = "진또배기 파스타";
            String category = "양식";
            String address = "서울시 강남구 테헤란로 212";
            Double latitude = 37.5012;
            Double longitude = 127.0396;
            String phoneNumber = "0212345678";
            LocalTime openTime = LocalTime.of(11, 30);
            LocalTime closeTime = LocalTime.of(21, 0);

            Store store = new Store(name, category, address, latitude, longitude,
                    phoneNumber, null, openTime, closeTime, null, "강남구");

            assertAll(
                    () -> assertThat(store.getName()).isEqualTo(name),
                    () -> assertThat(store.getCategory()).isEqualTo(StoreCategory.WESTERN),
                    () -> assertThat(store.getAddress()).isEqualTo(address),
                    () -> assertThat(store.getCoordinates().getLatitude()).isEqualTo(latitude),
                    () -> assertThat(store.getStorePhoneNumber().getValue()).isEqualTo(phoneNumber),
                    () -> assertThat(store.getStoreHours().getOpenTime()).isEqualTo(openTime)
            );
        }

        @Test
        void 가게_이름이_null이면_예외를_던진다() {
            assertThatThrownBy(() -> new Store(null, "양식", "주소", 37.5, 127.0, "0212345678", null, LocalTime.now(), LocalTime.now().plusHours(1), null, "강남구"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_NAME);
        }

        @Test
        void 주소가_null이면_예외를_던진다() {
            assertThatThrownBy(() -> new Store("가게", "양식", null, 37.5, 127.0, "0212345678", null, LocalTime.now(), LocalTime.now().plusHours(1), null, "강남구"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_ADDRESS);
        }

        @Test
        void 유효하지_않은_카테고리면_예외를_던진다() {
            assertThatThrownBy(() -> new Store("가게", "없는카테고리", "주소", 37.5, 127.0, "0212345678", null, LocalTime.now(), LocalTime.now().plusHours(1), null, "강남구"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_CATEGORY);
        }

        @Test
        void 좌표값이_null이면_예외를_던진다() {
            assertThatThrownBy(() -> new Store("가게", "양식", "주소", null, 127.0, "0212345678", null, LocalTime.now(), LocalTime.now().plusHours(1), null, "강남구"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_COORDINATES_NULL);
        }

        @Test
        void 전화번호_형식이_틀리면_예외를_던진다() {
            assertThatThrownBy(() -> new Store("가게", "양식", "주소", 37.5, 127.0, "123", null, LocalTime.now(), LocalTime.now().plusHours(1), null, "강남구"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_PHONE_NUMBER);
        }

        @Test
        void 영업_시간_순서가_틀리면_예외를_던진다() {
            LocalTime openTime = LocalTime.of(22, 0);
            LocalTime closeTime = LocalTime.of(10, 0);

            assertThatThrownBy(() -> new Store("가게", "양식", "주소", 37.5, 127.0, "0212345678", null, openTime, closeTime, null, "강남구"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_TIME_ORDER);
        }
    }
}
