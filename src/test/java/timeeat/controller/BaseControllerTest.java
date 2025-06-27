package timeeat.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import timeeat.DatabaseCleaner;
import timeeat.client.oauth.OauthClient;
import timeeat.client.oauth.OauthMemberInformation;
import timeeat.client.oauth.OauthToken;
import timeeat.controller.web.jwt.JwtManager;

@ExtendWith(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseControllerTest {

    private static final List<Filter> SPEC_FILTERS = List.of(new RequestLoggingFilter(), new ResponseLoggingFilter());

    private static final OauthToken DEFAULT_OAUTH_TOKEN = new OauthToken("oauth-access-token");
    private static final OauthMemberInformation DEFAULT_OAUTH_MEMBER_INFO =
            new OauthMemberInformation(123L, "nickname");

    @LocalServerPort
    private int port;

    @Autowired
    private JwtManager jwtManager;

    @MockitoBean
    private OauthClient oauthClient;

    private RequestSpecification spec;

    @BeforeEach
    final void setEnvironment() {
        RestAssured.port = port;
        spec = new RequestSpecBuilder()
                .addFilters(SPEC_FILTERS)
                .build();
    }

    @BeforeEach
    final void mockingClient() {
        doReturn(DEFAULT_OAUTH_TOKEN).when(oauthClient).requestOauthToken(anyString());
        doReturn(DEFAULT_OAUTH_MEMBER_INFO).when(oauthClient).requestMemberInformation(DEFAULT_OAUTH_TOKEN);
    }

    protected final RequestSpecification given() {
        return RestAssured.given(spec);
    }

    protected final String accessToken() {
        // TODO : 실제 회원 생성
        return jwtManager.issueAccessToken(1L);
    }

    protected final String refreshToken() {
        // TODO : 실제 회원 생성
        return jwtManager.issueRefreshToken(1L);
    }
}
