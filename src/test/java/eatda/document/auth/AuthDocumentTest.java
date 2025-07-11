package eatda.document.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import eatda.controller.auth.LoginRequest;
import eatda.controller.auth.ReissueRequest;
import eatda.controller.member.MemberResponse;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import io.restassured.http.ContentType;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

public class AuthDocumentTest extends BaseDocumentTest {

    @Value("${oauth.allowed-origins[0]}")
    private String origin;

    @Nested
    class RedirectOauthLoginPage {

        RestDocsRequest requestDocument = request()
                .tag(Tag.AUTH_API)
                .summary("OAuth 로그인 페이지 리다이렉트")
                .requestHeader(
                        headerWithName(HttpHeaders.REFERER).description("요청 Origin")
                );

        RestDocsResponse responseDocument = response()
                .responseHeader(
                        headerWithName(HttpHeaders.LOCATION).description("리다이렉트 URI")
                );

        @Test
        void Oauth_로그인_페이지_리다이렉트_성공() throws URISyntaxException {
            doReturn(new URI("http://localhost:8080")).when(authService).getOauthLoginUrl(anyString());

            var document = document("auth/oauth-redirect", 302)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .redirects().follow(false)
                    .header(HttpHeaders.REFERER, origin)
                    .when()
                    .get("/api/auth/login/oauth")
                    .then()
                    .statusCode(302);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"UNAUTHORIZED_ORIGIN"})
        @ParameterizedTest
        void Oauth_로그인_페이지_리다이렉트_실패(BusinessErrorCode errorCode) {
            doThrow(new BusinessException(errorCode)).when(authService).getOauthLoginUrl(anyString());

            var document = document("auth/oauth-redirect", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .header(HttpHeaders.REFERER, origin)
                    .when().get("/api/auth/login/oauth")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class Login {

        RestDocsRequest requestDocument = request()
                .tag(Tag.AUTH_API)
                .summary("로그인")
                .requestBodyField(
                        fieldWithPath("code").type(STRING).description("Oauth 인가 코드"),
                        fieldWithPath("origin").type(STRING).description("요청 Origin")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("token").type(OBJECT).description("토큰 정보"),
                        fieldWithPath("token.accessToken").type(STRING).description("액세스 토큰"),
                        fieldWithPath("token.refreshToken").type(STRING).description("리프레시 토큰"),
                        fieldWithPath("information").type(OBJECT).description("유저 정보"),
                        fieldWithPath("information.id").type(NUMBER).description("유저 식별자"),
                        fieldWithPath("information.email").type(STRING).description("유저 이메일"),
                        fieldWithPath("information.isSignUp").type(BOOLEAN).description("회원 가입 여부"),
                        fieldWithPath("information.nickname").type(STRING).description("유저 닉네임"),
                        fieldWithPath("information.phoneNumber").type(STRING).description("핸드폰 전화번호").optional(),
                        fieldWithPath("information.optInMarketing").type(BOOLEAN).description("마케팅 동의 여부").optional()
                );

        @Test
        void 로그인_성공() {
            LoginRequest request = new LoginRequest("code", "http://localhost:3000");
            MemberResponse response = new MemberResponse(1L, "abc@kakao.com", true, "닉네임", null, null);
            doReturn(response).when(authService).login(request);

            var document = document("auth/login", 201)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.ORIGIN, origin)
                    .body(request)
                    .when().post("/api/auth/login")
                    .then().statusCode(201);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"UNAUTHORIZED_ORIGIN", "OAUTH_SERVER_ERROR"})
        @ParameterizedTest
        void 로그인_실패(BusinessErrorCode errorCode) {
            LoginRequest request = new LoginRequest("code", "http://localhost:3000");
            doThrow(new BusinessException(errorCode)).when(authService).login(request);

            var document = document("auth/login", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/auth/login")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class ReissueToken {

        private final RestDocsRequest requestDocument = request()
                .tag(Tag.AUTH_API)
                .summary("토큰 재발급")
                .requestBodyField(
                        fieldWithPath("refreshToken").type(STRING).description("리프레시 토큰")
                );

        private final RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("accessToken").type(STRING).description("액세스 토큰"),
                        fieldWithPath("refreshToken").type(STRING).description("리프레시 토큰")
                );

        @Test
        void 토큰_재발급_성공() {
            ReissueRequest request = new ReissueRequest(refreshToken());

            var document = document("auth/reissue", 201)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/auth/reissue")
                    .then().statusCode(201);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"EXPIRED_TOKEN", "UNAUTHORIZED_MEMBER"})
        @ParameterizedTest
        void 토큰_재발급_실패(BusinessErrorCode errorCode) {
            ReissueRequest request = new ReissueRequest(refreshToken());
            doThrow(new BusinessException(errorCode)).when(jwtManager).resolveRefreshToken(request.refreshToken());

            var document = document("auth/reissue", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/auth/reissue")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }
}
