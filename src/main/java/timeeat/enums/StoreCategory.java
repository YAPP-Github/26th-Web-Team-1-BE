package timeeat.enums;

import java.util.Arrays;
import lombok.Getter;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Getter
public enum StoreCategory {

    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    CAFE("카페"),
    DESSERT("디저트"),
    PUB("술집"),
    FAST_FOOD("패스트푸드"),
    CONVENIENCE("편의점"),
    OTHER("기타");

    private final String categoryName;

    StoreCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public static StoreCategory from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_CATEGORY);
        }
        return Arrays.stream(values())
                .filter(category -> category.categoryName.equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_STORE_CATEGORY));
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
