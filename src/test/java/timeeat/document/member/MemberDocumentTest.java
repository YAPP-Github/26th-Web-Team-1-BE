package timeeat.document.member;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import timeeat.controller.member.MemberResponse;
import timeeat.controller.member.MemberUpdateRequest;
import timeeat.document.BaseDocumentTest;
import timeeat.document.RestDocsRequest;
import timeeat.document.RestDocsResponse;

public class MemberDocumentTest extends BaseDocumentTest {

    @Nested
    class updateMember {

        RestDocsRequest requestDocument = request()
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ).requestBodyField(
                        fieldWithPath("nickname").type(STRING).description("회원 닉네임"),
                        fieldWithPath("phoneNumber").type(STRING).description("회원 전화번호 ex) 01012345678"),
                        fieldWithPath("interestArea").type(STRING).description("회원 관심 지역 ex) 종로구"),
                        fieldWithPath("optInMarketing").type(BOOLEAN).description("마케팅 동의 여부")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("id").type(NUMBER).description("회원 식별자"),
                        fieldWithPath("isSignUp").type(BOOLEAN).description("회원 가입 요청 여부 (false 고정)"),
                        fieldWithPath("nickname").type(STRING).description("회원 닉네임").optional(),
                        fieldWithPath("phoneNumber").type(STRING).description("회원 전화번호 ex) 01012345678").optional(),
                        fieldWithPath("interestArea").type(STRING).description("회원 관심 지역 ex) 종로구").optional(),
                        fieldWithPath("optInMarketing").type(BOOLEAN).description("마케팅 동의 여부").optional()
                );

        @Test
        void 회원_정보_수정_성공() {
            MemberUpdateRequest request = new MemberUpdateRequest("update-nickname", "01012345678", "성북구", true);
            MemberResponse response = new MemberResponse(1L, false, "update-nickname", "01012345678", "성북구", true);
            doReturn(response).when(memberService).update(anyLong(), eq(request));

            var document = document("member/update", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .body(request)
                    .when().put("/api/member")
                    .then().statusCode(200);
        }
    }
}
