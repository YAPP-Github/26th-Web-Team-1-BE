package timeeat.domain.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

class CoordinatesTest {

    @Nested
    @DisplayName("좌표를 생성할 때")
    class CreateCoordinates {

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

            BusinessException exception = assertThrows(BusinessException.class, () -> new Coordinates(nullLatitude, longitude));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_STORE_COORDINATES_NULL);
        }

        @Test
        void 경도_값이_null이면_예외를_던진다() {
            Double latitude = 37.5665;
            Double nullLongitude = null;

            BusinessException exception = assertThrows(BusinessException.class, () -> new Coordinates(latitude, nullLongitude));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_STORE_COORDINATES_NULL);
        }

        @Test
        @DisplayName("위도 값이 서울 지역 범위를 벗어나면 예외를 던진다")
        void 위도값이_최소_범위를_벗어나면_예외를_던진다() {
            Double invalidLatitude = -91.0;
            Double validLongitude = 126.9780;

            BusinessException exception = assertThrows(BusinessException.class, () -> new Coordinates(invalidLatitude, validLongitude));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.OUT_OF_SEOUL_LATITUDE_RANGE);
        }

        @Test
        @DisplayName("경도 값이 서울 지역 범위를 벗어나면 예외를 던진다")
        void 경도값이_최대_범위를_벗어나면_예외를_던진다() {
            Double invalidLongitude = 181.0;
            Double validLatitude = 37.5665;

            BusinessException exception = assertThrows(BusinessException.class, () -> new Coordinates(validLatitude, invalidLongitude));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.OUT_OF_SEOUL_LONGITUDE_RANGE);
        }
    }
}
