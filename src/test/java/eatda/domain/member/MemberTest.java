package eatda.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    @Nested
    @DisplayName("Oauth 최초 가입으로 회원을 생성할 때")
    class CreateOauthMember {

        @Test
        void socialId_email_nickname만으로_생성_시_나머지_필드는_null_상태이다() {
            String socialId = "oauth-user-id";
            String email = "test@example.com";

            Member member = new Member(socialId, email, "nickname");

            assertAll(
                    () -> assertThat(member.getSocialId()).isEqualTo(socialId),
                    () -> assertThat(member.getEmail()).isEqualTo(email),
                    () -> assertThat(member.getNickname()).isNotNull(),
                    () -> assertThat(member.getMobilePhoneNumber()).isNull(),
                    () -> assertThat(member.getOptInMarketing()).isNull()
            );
        }

        @Test
        void socialId가_null이면_예외가_발생한다() {
            String socialId = null;
            String email = "test@example.com";

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new Member(socialId, email, "nickname"));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_SOCIAL_ID);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"abc.com", "invalid-email", "test@.com", "@example.com"})
        void email이_null이거나_유효하지_않으면_예외가_발생한다(String invalidEmail) {
            String socialId = "oauth-user-id";

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new Member(socialId, invalidEmail, "nickname"));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_EMAIL);
        }
    }

    @Nested
    @DisplayName("가입 완료 후 회원을 생성할 때")
    class CreateMemberTest {

        @Test
        void 모든_정보가_정상적일_때_회원을_생성한다() {
            String socialId = "test-social-id-123";
            String email = "test@example.com";
            String nickname = "맛있는녀석들-32";
            String mobilePhoneNumber = "01012345678";
            Boolean optInMarketing = true;

            Member member = new Member(socialId, email, nickname, mobilePhoneNumber, optInMarketing);

            assertAll(
                    () -> assertThat(member.getSocialId()).isEqualTo(socialId),
                    () -> assertThat(member.getEmail()).isEqualTo(email),
                    () -> assertThat(member.getNickname()).isEqualTo(nickname),
                    () -> assertThat(member.getMobilePhoneNumber().getValue()).isEqualTo(mobilePhoneNumber),
                    () -> assertThat(member.isOptInMarketing()).isTrue()
            );
        }

        @Test
        void 선택적_필드가_null이어도_멤버를_생성한다() {
            String socialId = "test-social-id-123";
            String email = "test@example.com";
            String nickname = null;
            String mobilePhoneNumber = null;
            Boolean optInMarketing = false;

            Member member = new Member(socialId, email, nickname, mobilePhoneNumber, optInMarketing);

            assertAll(
                    () -> assertThat(member.getNickname()).isNull(),
                    () -> assertThat(member.getMobilePhoneNumber().getValue()).isNull(),
                    () -> assertThat(member.isOptInMarketing()).isFalse()
            );
        }

        @Test
        void 가입_완료_시_마케팅_동의_여부가_null이면_예외를_던진다() {
            String socialId = "test-social-id-123";
            String email = "test@example.com";
            String nickname = "맛있는녀석들-32";
            String mobilePhoneNumber = "01012345678";
            Boolean optInMarketing = null;

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new Member(socialId, email, nickname, mobilePhoneNumber, optInMarketing));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_MARKETING_CONSENT);
        }
    }

    @Nested
    class UpdateMember {

        @Test
        void 회원_정보를_정상적으로_수정한다() {
            Member member = new Member("social-id", "abc@example.com", "nickname");
            Member updatedMember = new Member("new-nickname", "01012345678", true);

            member.update(updatedMember);

            assertAll(
                    () -> assertThat(member.getNickname()).isEqualTo("new-nickname"),
                    () -> assertThat(member.getPhoneNumber()).isEqualTo("01012345678"),
                    () -> assertThat(member.isOptInMarketing()).isTrue()
            );
        }
    }

    @Nested
    class IsSameNicknameTest {

        @Test
        void 동일한_닉네임을_비교하면_true를_반환한다() {
            Member member = new Member("social-id", "abc@example.com", "nickname");

            boolean result = member.isSameNickname("nickname");

            assertThat(result).isTrue();
        }

        @Test
        void 다른_닉네임을_비교하면_false를_반환한다() {
            Member member = new Member("social-id", "abc@example.com", "nickname");

            boolean result = member.isSameNickname("different-nickname");

            assertThat(result).isFalse();
        }
    }

    @Nested
    class IsSameMobilePhoneNumberTest {

        @Test
        void 동일한_전화번호를_비교하면_true를_반환한다() {
            Member member = new Member("social-id", "abc@example.com", "nickname", "01012345678", true);

            boolean result = member.isSameMobilePhoneNumber("01012345678");

            assertThat(result).isTrue();
        }

        @Test
        void 다른_전화번호를_비교하면_false를_반환한다() {
            Member member = new Member("social-id", "abc@example.com", "nickname", "01012345678", true);

            boolean result = member.isSameMobilePhoneNumber("01087654321");

            assertThat(result).isFalse();
        }
    }
}
