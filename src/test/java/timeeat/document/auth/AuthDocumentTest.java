package timeeat.document.auth;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import io.restassured.http.ContentType;
import timeeat.controller.auth.ReissueRequest;
import timeeat.document.BaseDocumentTest;
import timeeat.document.RestDocsRequest;
import timeeat.document.RestDocsResponse;
import timeeat.document.Tag;

public class AuthDocumentTest extends BaseDocumentTest {

    @Nested
    class RedirectOauthLoginPage {

        RestDocsRequest requestDocument = request()
                .tag(Tag.MEMBER_API)
                .summary("OAuth 로그인 페이지 리다이렉트");

        RestDocsResponse responseDocument = response()
                .responseHeader(
                        headerWithName(HttpHeaders.LOCATION).description("리다이렉트 URI")
                );

        @Test
        void Oauth_로그인_페이지로_리다이렉트_할_수_있다() {
            var document = document("member/oauth_redirect", 302)
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
    class ReissueToken {

        private final RestDocsRequest requestDocument = request()
                .tag(Tag.MEMBER_API)
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

            var document = document("member/reissue", 200)
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
