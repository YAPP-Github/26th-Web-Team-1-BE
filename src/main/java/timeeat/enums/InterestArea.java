package timeeat.enums;

import lombok.Getter;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import java.util.Arrays;

@Getter
public enum InterestArea {

    JONGNO("종로구"),
    JUNG("중구"),
    YONGSAN("용산구"),
    SEONGDONG("성동구"),
    GWANGJIN("광진구"),
    DONGDAEMUN("동대문구"),
    JUNGRANG("중랑구"),
    SEONGBUK("성북구"),
    GANGBUK("강북구"),
    DOBONG("도봉구"),
    NOWON("노원구"),
    EUNPYEONG("은평구"),
    SEODAEMUN("서대문구"),
    MAPO("마포구"),
    YANGCHEON("양천구"),
    GANGSEO("강서구"),
    GURO("구로구"),
    GEUMCHEON("금천구"),
    YEONGDEUNGPO("영등포구"),
    DONGJAK("동작구"),
    GWANAK("관악구"),
    SEOCHO("서초구"),
    GANGNAM("강남구"),
    SONGPA("송파구"),
    GANGDONG("강동구");

    private final String areaName;

    InterestArea(String areaName) {
        this.areaName = areaName;
    }

    public static InterestArea from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_INTEREST_AREA);
        }

        return Arrays.stream(values())
                .filter(area -> area.areaName.equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_INTEREST_AREA));
    }

    public static boolean isValid(String value) {
        try {
            from(value);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
}
