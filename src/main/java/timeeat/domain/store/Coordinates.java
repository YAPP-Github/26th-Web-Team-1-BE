package timeeat.domain.store;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Coordinates {

    private static final double MIN_LATITUDE = 37.413294;
    private static final double MAX_LATITUDE = 37.715133;
    private static final double MIN_LONGITUDE = 126.734086;
    private static final double MAX_LONGITUDE = 127.269311;

    private Double latitude;
    private Double longitude;

    public Coordinates(Double latitude, Double longitude) {
        validate(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validate(Double latitude, Double longitude) {
        if (latitude != null && (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE)) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_COORDINATES);
        }
        if (longitude != null && (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE)) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_COORDINATES);
        }
    }
}