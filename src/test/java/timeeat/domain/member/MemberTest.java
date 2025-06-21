package timeeat.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.enums.InterestArea;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberTest {

    @Nested
    @DisplayName("Oauth 최초 가입으로 회원을 생성할 때")
    class CreateOauthMemberTest {

        @Test
        void socialId만으로_생성_시_나머지_필드는_null_상태이다() {
            String socialId = "oauth-user-id";

            Member member = new Member(socialId);

            assertAll(
                    () -> assertThat(member.getSocialId()).isEqualTo(socialId),
                    () -> assertThat(member.getNickname()).isNull(),
                    () -> assertThat(member.getMobilePhoneNumber()).isNull(),
                    () -> assertThat(member.getInterestArea()).isNull(),
                    () -> assertThat(member.getOptInMarketing()).isNull()
            );
        }

        @Test
        void socialId가_null이면_예외가_발생한다() {
            String socialId = null;

            assertThatThrownBy(() -> new Member(socialId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_SOCIAL_ID);
        }
    }

    @Nested
    @DisplayName("가입 완료 후 회원을 생성할 때")
    class CreateMemberTest {

        @Test
        void 모든_정보가_정상적일_때_회원을_생성한다() {
            String socialId = "test-social-id-123";
            String nickname = "맛있는녀석들-32";
            String mobilePhoneNumber = "01012345678";
            String interestArea = "강남구";
            Boolean optInMarketing = true;

            Member member = new Member(socialId, nickname, mobilePhoneNumber, interestArea, optInMarketing);

            assertAll(
                    () -> assertThat(member.getSocialId()).isEqualTo(socialId),
                    () -> assertThat(member.getNickname()).isEqualTo(nickname),
                    () -> assertThat(member.getMobilePhoneNumber().getValue()).isEqualTo(mobilePhoneNumber),
                    () -> assertThat(member.getInterestArea()).isEqualTo(InterestArea.GANGNAM),
                    () -> assertThat(member.isOptInMarketing()).isTrue()
            );
        }

        @Test
        void 선택적_필드가_null이어도_멤버를_생성한다() {
            String socialId = "test-social-id-123";
            String nickname = null;
            String mobilePhoneNumber = null;
            String interestArea = "강남구";
            Boolean optInMarketing = false;

            Member member = new Member(socialId, nickname, mobilePhoneNumber, interestArea, optInMarketing);

            assertThat(member.getNickname()).isNull();
            assertThat(member.getMobilePhoneNumber().getValue()).isNull();
            assertThat(member.isOptInMarketing()).isFalse();
        }

        @Test
        void 가입_완료_시_마케팅_동의_여부가_null이면_예외를_던진다() {
            String socialId = "test-social-id-123";
            String nickname = "맛있는녀석들-32";
            String mobilePhoneNumber = "01012345678";
            String interestArea = "강남구";
            Boolean optInMarketing = null;

            assertThatThrownBy(() -> new Member(socialId, nickname, mobilePhoneNumber, interestArea, optInMarketing))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_MARKETING_CONSENT);
        }

        @Test
        void 유효하지_않은_관심_지역으로_생성하면_예외를_던진다() {
            String socialId = "test-social-id-123";
            String nickname = "맛있는녀석들-32";
            String mobilePhoneNumber = "01012345678";
            String interestArea = "부산";
            Boolean optInMarketing = true;

            assertThatThrownBy(() -> new Member(socialId, nickname, mobilePhoneNumber, interestArea, optInMarketing))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_INTEREST_AREA);
        }
    }
}