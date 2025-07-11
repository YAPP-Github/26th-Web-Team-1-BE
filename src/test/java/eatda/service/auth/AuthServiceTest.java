package eatda.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import eatda.client.oauth.OauthMemberInformation;
import eatda.client.oauth.OauthToken;
import eatda.controller.auth.LoginRequest;
import eatda.controller.member.MemberResponse;
import eatda.service.BaseServiceTest;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServiceTest extends BaseServiceTest {

    private static final OauthToken DEFAULT_OAUTH_TOKEN = new OauthToken("oauth-access-token");
    private static final OauthMemberInformation DEFAULT_OAUTH_MEMBER_INFO =
            new OauthMemberInformation(123L, "authService@kakao.com", "nickname");

    @Autowired
    private AuthService authService;

    @BeforeEach
    protected final void mockingClient() throws URISyntaxException {
        doReturn(new URI("http://localhost:8080/login/callback")).when(oauthClient).getOauthLoginUrl(anyString());
        doReturn(DEFAULT_OAUTH_TOKEN).when(oauthClient).requestOauthToken(anyString(), anyString());
        doReturn(DEFAULT_OAUTH_MEMBER_INFO).when(oauthClient).requestMemberInformation(DEFAULT_OAUTH_TOKEN);
    }

    @Nested
    class Login {

        @Test
        void 로그인_최초_요청_시_회원가입_및_로그인_처리를_한다() {
            LoginRequest request = new LoginRequest("auth_code", "http://localhost:3000");

            MemberResponse response = authService.login(request);

            assertAll(
                    () -> assertThat(response.isSignUp()).isTrue(),
                    () -> assertThat(response.nickname()).isNotNull(),
                    () -> assertThat(response.phoneNumber()).isNull(),
                    () -> assertThat(response.optInMarketing()).isNull()
            );
        }

        @Test
        void 로그인_최초_요청이_아닐_경우_로그인만_처리를_한다() {
            memberGenerator.generate("123");
            LoginRequest request = new LoginRequest("auth_code", "http://localhost:3000");

            MemberResponse response = authService.login(request);

            assertThat(response.isSignUp()).isFalse();
        }
    }
}
