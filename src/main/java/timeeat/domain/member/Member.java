package timeeat.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.enums.InterestArea;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Table(name = "member")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social_id", unique = true, nullable = false)
    private String socialId;

    @Column(name = "nickname")
    private String nickname;

    @Embedded
    private MobilePhoneNumber mobilePhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_area")
    private InterestArea interestArea;

    @Column(name = "opt_in_marketing")
    private Boolean optInMarketing;

    public Member(String socialId, String nickname) {
        validateSocialId(socialId);
        this.socialId = socialId;
        this.nickname = nickname;
    }

    public Member(
            String socialId,
            String nickname,
            String mobilePhoneNumber,
            String interestArea,
            Boolean optInMarketing
    ) {
        this(socialId, nickname);
        validateOptInMarketing(optInMarketing);
        this.mobilePhoneNumber = new MobilePhoneNumber(mobilePhoneNumber);
        this.interestArea = InterestArea.from(interestArea);
        this.optInMarketing = optInMarketing;
    }

    private void validateSocialId(String socialId) {
        if (socialId == null || socialId.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_SOCIAL_ID);
        }
    }

    private void validateOptInMarketing(Boolean optInMarketing) {
        if (optInMarketing == null) {
            throw new BusinessException(BusinessErrorCode.INVALID_MARKETING_CONSENT);
        }
    }

    public boolean isOptInMarketing() {
        return Boolean.TRUE.equals(optInMarketing);
    }

    public String getPhoneNumber() {
        return mobilePhoneNumber.getValue();
    }

    public String getInterestAreaName() {
        return interestArea.getAreaName();
    }
}

