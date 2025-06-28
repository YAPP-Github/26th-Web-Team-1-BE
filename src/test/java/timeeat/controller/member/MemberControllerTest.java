package timeeat.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import timeeat.controller.BaseControllerTest;

class MemberControllerTest extends BaseControllerTest {

    @Nested
    class UpdateMember {

        @Test
        void 회원_정보를_수정할_수_있다() {
            MemberUpdateRequest request = new MemberUpdateRequest("update-nickname", "01012345678", "성북구", true);

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
                    () -> assertThat(response.interestArea()).isEqualTo("성북구"),
                    () -> assertThat(response.optInMarketing()).isTrue()
            );
        }
    }

}
