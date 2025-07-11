package eatda.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.BaseControllerTest;
import eatda.domain.member.Member;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class MemberControllerTest extends BaseControllerTest {

    @Nested
    class GetMember {

        @Test
        void 회원_정보를_조회할_수_있다() {
            Member member = memberGenerator.generateRegisteredMember(
                    "123", "abc@kakao.com", "test-nickname", "01012345678");
            String accessToken = jwtManager.issueAccessToken(member.getId());

            MemberResponse response = given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when().get("/api/member")
                    .then()
                    .statusCode(200)
                    .extract().as(MemberResponse.class);

            assertAll(
                    () -> assertThat(response.id()).isEqualTo(member.getId()),
                    () -> assertThat(response.nickname()).isEqualTo(member.getNickname()),
                    () -> assertThat(response.phoneNumber()).isEqualTo(member.getPhoneNumber()),
                    () -> assertThat(response.isSignUp()).isFalse()
            );
        }
    }

    @Nested
    class CheckNickname {

        @Test
        void 중복되지_않는_닉네임을_확인할_수_있다() {
            given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("nickname", "new-nickname")
                    .when().get("/api/member/nickname/check")
                    .then()
                    .statusCode(204);
        }

        @Test
        void 중복된_닉네임을_확인할_수_있다() {
            String existingNickname = "existing-nickname";
            memberGenerator.generateRegisteredMember(existingNickname, "hij@kakao.com", "123", "01012345678");

            given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("nickname", existingNickname)
                    .when().get("/api/member/nickname/check")
                    .then()
                    .statusCode(400);
        }
    }

    @Nested
    class CheckPhoneNumber {

        @Test
        void 중복되지_않는_전화번호를_확인할_수_있다() {
            given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("phoneNumber", "01098765432")
                    .when().get("/api/member/phone-number/check")
                    .then()
                    .statusCode(204);
        }

        @Test
        void 중복된_전화번호를_확인할_수_있다() {
            String existingPhoneNumber = "01012345678";
            memberGenerator.generateRegisteredMember("nickname", "hij@kakao.com", "123", existingPhoneNumber);
            given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("phoneNumber", existingPhoneNumber)
                    .when().get("/api/member/phone-number/check")
                    .then()
                    .statusCode(400);
        }
    }

    @Nested
    class UpdateMember {

        @Test
        void 회원_정보를_수정할_수_있다() {
            MemberUpdateRequest request = new MemberUpdateRequest("update-nickname", "01012345678", true);

            MemberResponse response = given()
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .body(request)
                    .when().put("/api/member")
                    .then()
                    .statusCode(200)
                    .extract().as(MemberResponse.class);

            assertAll(
                    () -> assertThat(response.isSignUp()).isFalse(),
                    () -> assertThat(response.nickname()).isEqualTo("update-nickname"),
                    () -> assertThat(response.phoneNumber()).isEqualTo("01012345678"),
                    () -> assertThat(response.optInMarketing()).isTrue()
            );
        }
    }

}
