package timeeat.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorCode {

    // Member
    INVALID_MOBILE_PHONE_NUMBER("MEM001", "전화번호는 11자리여야 합니다."),
    INVALID_INTEREST_AREA("MEM002", "유효하지 않은 관심 지역입니다."),
    INVALID_MARKETING_CONSENT("MEM003", "마케팅 동의 여부는 필수입니다."),
    INVALID_MEMBER_ID("MEM004", "유효하지 않은 회원 ID입니다."),
    INVALID_SOCIAL_ID("MEM005", "소셜 ID는 필수입니다."),

    // Store
    INVALID_STORE_CATEGORY("STO001", "유효하지 않은 매장 카테고리입니다."),
    INVALID_STORE_NAME("STO002", "매장명은 필수입니다."),
    INVALID_STORE_ADDRESS("STO003", "매장 주소는 필수입니다."),
    INVALID_STORE_PHONE_NUMBER("STO004", "매장 전화번호는 9~12자리여야 합니다."),
    INVALID_STORE_COORDINATES("STO005", "유효하지 않은 좌표입니다."),
    INVALID_STORE_ID("STO006", "유효하지 않은 매장 ID입니다."),
    INVALID_STORE_COORDINATES_NULL("STO007", "좌표 값은 필수입니다."),
    INVALID_STORE_TIME_NULL("STO008", "영업 시간은 필수입니다."),
    INVALID_STORE_TIME_ORDER("STO09", "종료 시간은 시작 시간보다 늦어야 합니다."),

    // Menu
    INVALID_MENU_NAME("MEN001", "메뉴명은 필수입니다."),
    INVALID_MENU_PRICE("MEN002", "메뉴 가격은 0보다 커야 합니다."),
    INVALID_MENU_DISCOUNT_PRICE("MEN003", "할인 가격은 원가보다 작아야 합니다."),
    INVALID_MENU_DISCOUNT_TIME("MEN004", "할인 시간이 올바르지 않습니다."),

    // Bookmark
    DUPLICATE_BOOKMARK("BOK001", "이미 북마크된 매장입니다."),
    BOOKMARK_NOT_FOUND("BOK002", "북마크를 찾을 수 없습니다."),
    BOOKMARK_MEMBER_REQUIRED("BOK003", "북마크 생성 시 회원 정보는 필수입니다."),
    BOOKMARK_STORE_REQUIRED("BOK004", "북마크 생성 시 가게 정보는 필수입니다.");

    private final String code;
    private final String message;

    BusinessErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
