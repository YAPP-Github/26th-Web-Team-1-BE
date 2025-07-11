package eatda.client.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
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

    private void setMockServer(HttpMethod method, String uri, String responseBody) {
        mockServer.expect(requestTo(uri))
                .andExpect(method(method))
                .andRespond(MockRestResponseCreators.withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }

    @Nested
    class GetOauthLoginUrl {

        @Test
        void Oauth_로그인_URL을_생성할_수_있다() {
            String origin = properties.getAllowedOrigins().getFirst();
            String redirectUri = origin + properties.getRedirectPath();

            URI uri = oauthClient.getOauthLoginUrl(origin);

            assertAll(
                    () -> assertThat(uri.getHost()).isEqualTo("kauth.kakao.com"),
                    () -> assertThat(uri.getPath()).isEqualTo("/oauth/authorize"),
                    () -> assertThat(uri.getQuery()).contains("client_id=%s&".formatted(properties.getClientId())),
                    () -> assertThat(uri.getQuery()).contains("redirect_uri=%s&".formatted(redirectUri)),
                    () -> assertThat(uri.getQuery()).contains("response_type=code")
            );
        }

        @Test
        void 허용된_Origin이_아니라면_예외를_발생시킨다() {
            String origin = "https://not-allowed-origin.com";

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> oauthClient.getOauthLoginUrl(origin));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.UNAUTHORIZED_ORIGIN);
        }

        @Test
        void origin_뒤에_슬래시가_붙어도_정상적으로_처리된다() {
            String origin = properties.getAllowedOrigins().getFirst();
            String redirectUri = origin + properties.getRedirectPath();

            URI uri = oauthClient.getOauthLoginUrl(origin + "/");

            assertThat(uri.getQuery()).contains("redirect_uri=%s&".formatted(redirectUri));
        }
    }

    @Nested
    class RequestOauthToken {

        @Test
        void Oauth_토큰을_요청할_수_있다() {
            String origin = properties.getAllowedOrigins().getFirst();
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

            OauthToken token = oauthClient.requestOauthToken(code, origin);

            assertThat(token.accessToken()).isEqualTo("test-access-token");
        }

        @Test
        void 허용된_오리진이_아니라면_예외를_발생시킨다() {
            String origin = "https://not-allowed-origin.com";
            String code = "test_code";

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> oauthClient.requestOauthToken(code, origin));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.UNAUTHORIZED_ORIGIN);
        }
    }

    @Nested
    class RequestMemberInformation {

        @Test
        void Oauth_회원정보를_요청할_수_있다() {
            setMockServer(HttpMethod.GET, "https://kapi.kakao.com/v2/user/me", """
                    {
                        "id": 123456789,
                        "connected_at": "2025-07-08T13:31:28Z",
                        "properties": {
                            "nickname": "이충안"
                        },
                        "kakao_account": {
                            "profile_nickname_needs_agreement": false,
                            "profile": {
                                "nickname": "이충안",
                                "is_default_nickname": false
                            },
                            "has_email": true,
                            "email_needs_agreement": false,
                            "is_email_valid": true,
                            "is_email_verified": true,
                            "email": "cnddkscndgus@naver.com"
                        }
                    }""");
            OauthToken token = new OauthToken("test-access-token");

            OauthMemberInformation memberInfo = oauthClient.requestMemberInformation(token);

            assertAll(
                    () -> assertThat(memberInfo.socialId()).isEqualTo(123456789L),
                    () -> assertThat(memberInfo.email()).isEqualTo("cnddkscndgus@naver.com"),
                    () -> assertThat(memberInfo.nickname()).isEqualTo("이충안")
            );
        }
    }
}
