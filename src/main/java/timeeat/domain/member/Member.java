package timeeat.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.enums.InterestArea;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
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

    public Member(String socialId) {
        validateSocialId(socialId);
        this.socialId = socialId;
    }

    public Member(
            String socialId,
            String nickname,
            String mobilePhoneNumber,
            String interestArea,
            Boolean optInMarketing
    ) {
        this(socialId);
        validateOptInMarketing(optInMarketing);
        this.nickname = nickname;
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
}
