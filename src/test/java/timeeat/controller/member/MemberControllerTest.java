package timeeat.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import timeeat.controller.BaseControllerTest;

class MemberControllerTest extends BaseControllerTest {

    @Nested
    class RedirectOauthLoginPage {

        @Value("${oauth.clientId}")
        private String clientId;

        @Value("${oauth.redirectUri}")
        private String redirectUri;

        @Test
        void Oauth_로그인_페이지로_리다이렉트_할_수_있다() {
            String location = given()
                    .redirects().follow(false)
                    .when()
                    .get("/api/member/login/auth")
                    .then()
                    .statusCode(302)
                    .extract().header(HttpHeaders.LOCATION);

            assertThat(location)
                    .contains("https://kauth.kakao.com/oauth/authorize")
                    .contains("client_id=%s".formatted(clientId))
                    .contains("redirect_uri=%s".formatted(redirectUri))
                    .contains("response_type=code");
        }
    }

    @Nested
    class ReissueToken {

        @Test
        void 리프레시_토큰을_이용해_토큰을_재발급한다() {
            ReissueRequest request = new ReissueRequest(refreshToken());

            TokenResponse response = given().body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/api/member/reissue")
                    .then()
                    .statusCode(200)
                    .extract().as(TokenResponse.class);

            assertAll(
                    () -> assertThat(response.accessToken()).isNotBlank(),
                    () -> assertThat(response.refreshToken()).isNotBlank()
            );
        }
    }
}
