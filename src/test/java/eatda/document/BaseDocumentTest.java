package eatda.document;

import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import eatda.controller.web.jwt.JwtManager;
import eatda.exception.BusinessErrorCode;
import eatda.exception.EtcErrorCode;
import eatda.service.auth.AuthService;
import eatda.service.member.MemberService;
import eatda.service.store.CheerService;
import eatda.service.store.StoreService;
import eatda.service.story.StoryService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationConfigurer;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseDocumentTest {

    protected static final RestDocsResponse ERROR_RESPONSE = new RestDocsResponse()
            .responseBodyField(
                    fieldWithPath("errorCode").type(STRING).description("에러 코드"),
                    fieldWithPath("message").type(STRING).description("에러 메시지")
            );
    private static final String MOCKED_ACCESS_TOKEN = "access-token";
    private static final String MOCKED_REFRESH_TOKEN = "refresh-token";

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected MemberService memberService;

    @MockitoBean
    protected StoreService storeService;

    @MockitoBean
    protected StoryService storyService;

    @MockitoBean
    protected CheerService cheerService;

    @MockitoBean
    protected JwtManager jwtManager;

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    void setEnvironment(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        RestAssuredRestDocumentationConfigurer webConfigurer =
                RestAssuredRestDocumentation.documentationConfiguration(restDocumentation);
        spec = new RequestSpecBuilder()
                .addFilter(webConfigurer)
                .build();
    }

    @BeforeEach
    void mockingJwtManager() {
        doReturn(MOCKED_ACCESS_TOKEN).when(jwtManager).issueAccessToken(1L);
        doReturn(MOCKED_REFRESH_TOKEN).when(jwtManager).issueRefreshToken(1L);
        doReturn(1L).when(jwtManager).resolveAccessToken(MOCKED_ACCESS_TOKEN);
        doReturn(1L).when(jwtManager).resolveRefreshToken(MOCKED_REFRESH_TOKEN);
    }

    protected final RestDocsRequest request() {
        return new RestDocsRequest();
    }

    protected final RestDocsResponse response() {
        return new RestDocsResponse();
    }

    protected final RestDocsFilterBuilder document(String identifierPrefix, int statusCode) {
        return new RestDocsFilterBuilder(identifierPrefix, Integer.toString(statusCode));
    }

    protected final RestDocsFilterBuilder document(String identifierPrefix, BusinessErrorCode errorCode) {
        return new RestDocsFilterBuilder(identifierPrefix, errorCode.name());
    }

    protected final RequestSpecification given(RestDocumentationFilter documentationFilter) {
        return RestAssured.given(spec)
                .filter(documentationFilter);
    }

    protected final RestDocsFilterBuilder document(String identifierPrefix, EtcErrorCode errorCode) {
        return new RestDocsFilterBuilder(identifierPrefix, errorCode.name());
    }

    protected final String accessToken() {
        return MOCKED_ACCESS_TOKEN;
    }

    protected final String refreshToken() {
        return MOCKED_REFRESH_TOKEN;
    }
}
