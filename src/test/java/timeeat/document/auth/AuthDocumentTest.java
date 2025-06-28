package timeeat.document.auth;

import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import io.restassured.http.ContentType;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import timeeat.controller.auth.LoginRequest;
import timeeat.controller.auth.ReissueRequest;
import timeeat.controller.member.MemberResponse;
import timeeat.document.BaseDocumentTest;
import timeeat.document.RestDocsRequest;
import timeeat.document.RestDocsResponse;
import timeeat.document.Tag;

public class AuthDocumentTest extends BaseDocumentTest {

    @Nested
    class RedirectOauthLoginPage {

        RestDocsRequest requestDocument = request()
                .tag(Tag.AUTH_API)
                .summary("OAuth 로그인 페이지 리다이렉트");

        RestDocsResponse responseDocument = response()
                .responseHeader(
                        headerWithName(HttpHeaders.LOCATION).description("리다이렉트 URI")
                );

        @Test
        void Oauth_로그인_페이지로_리다이렉트_할_수_있다() throws URISyntaxException {
            doReturn(new URI("http://localhost:8080")).when(authService).getOauthLoginUrl();

            var document = document("auth/oauth_redirect", 302)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .redirects().follow(false)
                    .when()
                    .get("/api/auth/login/oauth")
                    .then()
                    .statusCode(302);
        }
    }

    @Nested
    class Login {

        RestDocsRequest requestDocument = request()
                .tag(Tag.AUTH_API)
                .summary("로그인")
                .requestBodyField(
                        fieldWithPath("code").type(STRING).description("Oauth 인가 코드")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("token").type(OBJECT).description("토큰 정보"),
                        fieldWithPath("token.accessToken").type(STRING).description("액세스 토큰"),
                        fieldWithPath("token.refreshToken").type(STRING).description("리프레시 토큰"),
                        fieldWithPath("information").type(OBJECT).description("유저 정보"),
                        fieldWithPath("information.id").type(NUMBER).description("유저 식별자"),
                        fieldWithPath("information.isSignUp").type(BOOLEAN).description("회원 가입 여부"),
                        fieldWithPath("information.nickname").type(STRING).description("유저 닉네임"),
                        fieldWithPath("information.phoneNumber").type(STRING).description("핸드폰 전화번호").optional(),
                        fieldWithPath("information.interestArea").type(STRING).description("유저 관심 지역").optional(),
                        fieldWithPath("information.optInMarketing").type(BOOLEAN).description("마케팅 동의 여부").optional()
                );

        @Test
        void 로그인_성공() {
            LoginRequest request = new LoginRequest("code");
            MemberResponse response = new MemberResponse(1L, true, "닉네임", null, null, null);
            doReturn(response).when(authService).login(request);

            var document = document("auth/login", 201)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/auth/login")
                    .then().statusCode(201);
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
    }
}
