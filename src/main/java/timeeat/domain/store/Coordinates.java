package timeeat.domain.store;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinates {

    private static final double MIN_LATITUDE = 37.413294;
    private static final double MAX_LATITUDE = 37.715133;
    private static final double MIN_LONGITUDE = 126.734086;
    private static final double MAX_LONGITUDE = 127.269311;
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    public Coordinates(Double latitude, Double longitude) {
        validate(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validate(Double latitude, Double longitude) {
        validateNotNull(latitude, longitude);
        validateRange(latitude, longitude);
    }

    private void validateNotNull(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_COORDINATES_NULL);
        }
    }

    private void validateRange(Double latitude, Double longitude) {
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_SEOUL_LATITUDE_RANGE);
        }
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_SEOUL_LONGITUDE_RANGE);
        }
    }
}
