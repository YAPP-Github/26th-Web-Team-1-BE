package timeeat.domain.member;

import java.util.regex.Pattern;
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
public class MobilePhoneNumber {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\d{11}$");

    @Column(name = "phone_number", nullable = false)
    private String value;

    public MobilePhoneNumber(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String number) {
        if (number != null && !PHONE_NUMBER_PATTERN.matcher(number).matches()) {
            throw new BusinessException(BusinessErrorCode.INVALID_MOBILE_PHONE_NUMBER);
        }
    }
}
