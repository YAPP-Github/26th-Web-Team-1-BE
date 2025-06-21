package timeeat.domain.menu;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Discount {
    private static final int MIN_PRICE = 1;

    private Integer discountPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Discount(Price originalPrice, Integer discountPrice, LocalDateTime startTime, LocalDateTime endTime) {
        validatePrice(originalPrice, discountPrice);
        validateTime(startTime, endTime);
        this.discountPrice = discountPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private void validatePrice(Price originalPrice, Integer discountPrice) {
        if (discountPrice != null) {
            if (discountPrice < MIN_PRICE || discountPrice >= originalPrice.getValue()) {
                throw new BusinessException(BusinessErrorCode.INVALID_MENU_DISCOUNT_PRICE);
            }
        }
    }

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException(BusinessErrorCode.INVALID_MENU_DISCOUNT_TIME);
        }
    }
}
