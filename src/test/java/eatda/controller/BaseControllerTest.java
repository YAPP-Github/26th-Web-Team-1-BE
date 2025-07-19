package eatda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import eatda.DatabaseCleaner;
import eatda.client.map.MapClient;
import eatda.client.map.StoreSearchResult;
import eatda.client.oauth.OauthClient;
import eatda.client.oauth.OauthMemberInformation;
import eatda.client.oauth.OauthToken;
import eatda.controller.web.jwt.JwtManager;
import eatda.domain.member.Member;
import eatda.fixture.CheerGenerator;
import eatda.fixture.MemberGenerator;
import eatda.fixture.StoreGenerator;
import eatda.repository.image.ImageRepository;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.CheerRepository;
import eatda.repository.store.StoreRepository;
import eatda.service.story.StoryService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseControllerTest {

    private static final List<Filter> SPEC_FILTERS = List.of(new RequestLoggingFilter(), new ResponseLoggingFilter());

    private static final OauthToken DEFAULT_OAUTH_TOKEN = new OauthToken("oauth-access-token");
    private static final OauthMemberInformation DEFAULT_OAUTH_MEMBER_INFO =
            new OauthMemberInformation(314159248183772L, "constant@kakao.com", "nickname");
    private static final String MOCKED_IMAGE_KEY = "mocked-image-path";
    private static final String MOCKED_IMAGE_URL = "https://example.com/image.jpg";


    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected StoreGenerator storeGenerator;

    @Autowired
    protected CheerGenerator cheerGenerator;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected StoreRepository storeRepository;

    @Autowired
    protected CheerRepository cheerRepository;

    @Autowired
    protected JwtManager jwtManager;

    @MockitoBean
    private OauthClient oauthClient;

    @MockitoBean
    private MapClient mapClient;

    @MockitoBean
    private ImageRepository imageRepository;

    @MockitoBean
    protected StoryService storyService; // TODO 실 객체로 변환

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    final void setEnvironment() {
        RestAssured.port = port;
        spec = new RequestSpecBuilder()
                .addFilters(SPEC_FILTERS)
                .build();
    }

    @BeforeEach
    final void mockingClient() throws URISyntaxException {
        doReturn(new URI("http://localhost:8080/login/callback")).when(oauthClient).getOauthLoginUrl(anyString());
        doReturn(DEFAULT_OAUTH_TOKEN).when(oauthClient).requestOauthToken(anyString(), anyString());
        doReturn(DEFAULT_OAUTH_MEMBER_INFO).when(oauthClient).requestMemberInformation(DEFAULT_OAUTH_TOKEN);

        List<StoreSearchResult> searchResults = List.of(
                new StoreSearchResult("123", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 본점", "https://yapp.co.kr",
                        "서울 강남구 대치동 896-33", "서울 강남구 선릉로86길 40-4", 37.0d, 128.0d),
                new StoreSearchResult("456", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 시청점", "http://yapp.kr",
                        "서울 중구 북창동 19-4", null, 37.0d, 128.0d)
        );
        doReturn(searchResults).when(mapClient).searchShops(anyString());

        doReturn(MOCKED_IMAGE_URL).when(imageRepository).getPresignedUrl(anyString());
        doReturn(MOCKED_IMAGE_KEY).when(imageRepository).upload(any(), any());
    }

    protected final RequestSpecification given() {
        return RestAssured.given(spec);
    }

    protected final String accessToken() {
        Member member = memberGenerator.generateByEmail(Long.toString(DEFAULT_OAUTH_MEMBER_INFO.socialId()),
                "authAccessToken@example.com");
        return jwtManager.issueAccessToken(member.getId());
    }

    protected final String refreshToken() {
        Member member = memberGenerator.generateByEmail(Long.toString(DEFAULT_OAUTH_MEMBER_INFO.socialId()),
                "authRefreshToken@example.com");
        return jwtManager.issueRefreshToken(member.getId());
    }

    protected final String oauthLoginSocialId() {
        return Long.toString(DEFAULT_OAUTH_MEMBER_INFO.socialId());
    }
}
