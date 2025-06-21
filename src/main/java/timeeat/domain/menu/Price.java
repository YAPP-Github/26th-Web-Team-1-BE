package timeeat.domain.menu;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Price {

    private static final int MIN_PRICE = 1;

    @Column(name = "price", nullable = false)
    private Integer value;

    public Price(Integer value) {
        validateMinPrice(value);
        this.value = value;
    }

    private void validateMinPrice(Integer value) {
        if (value == null || value < MIN_PRICE) {
            throw new BusinessException(BusinessErrorCode.INVALID_MENU_PRICE);
        }
    }
}
