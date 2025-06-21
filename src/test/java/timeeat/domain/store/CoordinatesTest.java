package timeeat.domain.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinatesTest {

    @Nested
    @DisplayName("좌표를 생성할 때")
    class CreateCoordinatesTest {

        @Test
        void 정상적인_위도와_경도로_생성한다() {
            Double latitude = 37.5665;
            Double longitude = 126.9780;

            Coordinates coordinates = new Coordinates(latitude, longitude);

            assertThat(coordinates.getLatitude()).isEqualTo(latitude);
            assertThat(coordinates.getLongitude()).isEqualTo(longitude);
        }

        @Test
        void 위도_값이_null이면_예외를_던진다() {
            Double nullLatitude = null;
            Double longitude = 126.9780;

            assertThatThrownBy(() -> new Coordinates(nullLatitude, longitude))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_COORDINATES_NULL);
        }

        @Test
        void 경도_값이_null이면_예외를_던진다() {
            Double latitude = 37.5665;
            Double nullLongitude = null;

            assertThatThrownBy(() -> new Coordinates(latitude, nullLongitude))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_STORE_COORDINATES_NULL);
        }
    }
}
