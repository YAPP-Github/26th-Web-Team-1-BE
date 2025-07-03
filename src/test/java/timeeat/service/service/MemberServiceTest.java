package timeeat.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import timeeat.controller.member.MemberResponse;
import timeeat.controller.member.MemberUpdateRequest;
import timeeat.domain.member.Member;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;
import timeeat.service.BaseServiceTest;

class MemberServiceTest extends BaseServiceTest {

    @Autowired
    private MemberService memberService;

    @Nested
    class ValidateNickname {

        @Test
        void 중복되지_않은_닉네임이면_예외가_발생하지_않는다() {
            memberGenerator.generate("123", "nickname");
            Member member = memberGenerator.generate("456", "unique-nickname");
            String newNickname = "new-unique-nickname";

            assertThatCode(() -> memberService.validateNickname(newNickname, member.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        void 자신의_기존_닉네임이면_예외가_발생하지_않는다() {
            Member member = memberGenerator.generate("123", "nickname");
            String newNickname = "nickname";

            assertThatCode(() -> memberService.validateNickname(newNickname, member.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        void 중복된_닉네임이_있으면_예외가_발생한다() {
            memberGenerator.generate("123", "duplicate-nickname");
            Member member = memberGenerator.generate("456", "another-nickname");
            String newNickname = "duplicate-nickname";

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> memberService.validateNickname(newNickname, member.getId()));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @Nested
    class ValidatePhoneNumber {

        @Test
        void 중복되지_않은_전화번호이면_예외가_발생하지_않는다() {
            memberGenerator.generate("123", "nickname");
            Member member = memberGenerator.generate("456", "unique-nickname");
            String newPhoneNumber = "01012345678";

            assertThatCode(() -> memberService.validatePhoneNumber(newPhoneNumber, member.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        void 자신의_기존_전화번호이면_예외가_발생하지_않는다() {
            Member member = memberGenerator.generateRegisteredMember("123", "nickname", "01012345678");
            String newPhoneNumber = "01012345678";

            assertThatCode(() -> memberService.validatePhoneNumber(newPhoneNumber, member.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        void 중복된_전화번호가_있으면_예외가_발생한다() {
            memberGenerator.generateRegisteredMember("123", "nickname1", "01012345678");
            Member member = memberGenerator.generateRegisteredMember("456", "nickname2", "01087654321");
            String newPhoneNumber = "01012345678";

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> memberService.validatePhoneNumber(newPhoneNumber, member.getId()));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.DUPLICATE_PHONE_NUMBER);
        }
    }

    @Nested
    class Update {

        @Test
        void 회원_정보를_수정할_수_있다() {
            Member member = memberGenerator.generate("123");
            MemberUpdateRequest request = new MemberUpdateRequest("update-nickname", "01012345678", true);

            MemberResponse response = memberService.update(member.getId(), request);

            assertAll(
                    () -> assertThat(response.id()).isEqualTo(member.getId()),
                    () -> assertThat(response.isSignUp()).isFalse(),
                    () -> assertThat(response.nickname()).isEqualTo("update-nickname"),
                    () -> assertThat(response.phoneNumber()).isEqualTo("01012345678"),
                    () -> assertThat(response.optInMarketing()).isTrue()
            );
        }

        @Test
        void 중복된_닉네임이_있으면_예외가_발생한다() {
            Member existMember = memberGenerator.generate("123", "duplicate-nickname");
            Member updatedMember = memberGenerator.generate("456");
            MemberUpdateRequest request =
                    new MemberUpdateRequest(existMember.getNickname(), "01012345678", true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> memberService.update(updatedMember.getId(), request));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.DUPLICATE_NICKNAME);
        }

        @Test
        void 기존의_닉네임과_동일하면_있으면_정상적으로_회원_정보가_수정된다() {
            Member member = memberGenerator.generate("123", "duplicate-nickname");
            MemberUpdateRequest request =
                    new MemberUpdateRequest(member.getNickname(), "01012345678", true);

            MemberResponse response = memberService.update(member.getId(), request);

            assertThat(response.nickname()).isEqualTo(request.nickname());
        }

        @Test
        void 중복된_전화번호가_있으면_예외가_발생한다() {
            String phoneNumber = "01012345678";
            memberGenerator.generateRegisteredMember("123", "nickname1", phoneNumber);
            Member updatedMember = memberGenerator.generate("456", "nickname2");
            MemberUpdateRequest request =
                    new MemberUpdateRequest("new-nickname", phoneNumber, true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> memberService.update(updatedMember.getId(), request));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        @Test
        void 기존의_전화번호와_동일하면_정상적으로_회원_정보가_수정된다() {
            String phoneNumber = "01012345678";
            Member member = memberGenerator.generateRegisteredMember("123", "nickname1", phoneNumber);
            MemberUpdateRequest request =
                    new MemberUpdateRequest("new-nickname", phoneNumber, true);

            MemberResponse response = memberService.update(member.getId(), request);

            assertThat(response.phoneNumber()).isEqualTo(phoneNumber);
        }
    }
}
