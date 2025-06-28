package timeeat.client.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.net.URI;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

@RestClientTest(OauthClient.class)
class OauthClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private OauthClient oauthClient;

    @Autowired
    private OauthProperties properties;

    public void setMockServer(HttpMethod method, String uri, String responseBody) {
        mockServer.expect(requestTo(uri))
                .andExpect(method(method))
                .andRespond(MockRestResponseCreators.withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }

    @Nested
    class GetOauthLoginUrl {

        @Test
        void Oauth_로그인_URL을_생성할_수_있다() {
            URI uri = oauthClient.getOauthLoginUrl();

            assertAll(
                    () -> assertThat(uri.getHost()).isEqualTo("kauth.kakao.com"),
                    () -> assertThat(uri.getPath()).isEqualTo("/oauth/authorize"),
                    () -> assertThat(uri.getQuery()).contains("client_id=%s".formatted(properties.getClientId())),
                    () -> assertThat(uri.getQuery()).contains("redirect_uri=%s".formatted(properties.getRedirectUri())),
                    () -> assertThat(uri.getQuery()).contains("response_type=code")
            );
        }
    }

    @Nested
    class RequestOauthToken {

        @Test
        void Oauth_토큰을_요청할_수_있다() {
            setMockServer(HttpMethod.POST, "https://kauth.kakao.com/oauth/token", """
                    {
                        "token_type":"bearer",
                        "access_token":"test-access-token",
                        "expires_in":43199,
                        "refresh_token":"test-refresh-token",
                        "refresh_token_expires_in":5184000,
                        "scope":"account_email profile"
                    }""");
            String code = "test_code";

            OauthToken token = oauthClient.requestOauthToken(code);

            assertThat(token.accessToken()).isEqualTo("test-access-token");
        }
    }

    @Nested
    class RequestMemberInformation {

        @Test
        void Oauth_회원정보를_요청할_수_있다() {
            setMockServer(HttpMethod.GET, "https://kapi.kakao.com/v2/user/me", """
                    {
                        "id":123456789,
                        "connected_at": "2022-04-11T01:45:28Z",
                        "kakao_account": {
                            "profile_nickname_needs_agreement": false,
                            "profile_image_needs_agreement": false,
                            "profile": {
                                "nickname": "홍길동",
                                "is_default_nickname": false
                            }
                        }
                    }""");
            OauthToken token = new OauthToken("test-access-token");

            OauthMemberInformation memberInfo = oauthClient.requestMemberInformation(token);

            assertAll(
                    () -> assertThat(memberInfo.socialId()).isEqualTo(123456789L),
                    () -> assertThat(memberInfo.nickname()).isEqualTo("홍길동")
            );
        }
    }
}
