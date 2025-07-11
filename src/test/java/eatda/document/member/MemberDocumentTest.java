package eatda.document.member;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import eatda.controller.member.MemberResponse;
import eatda.controller.member.MemberUpdateRequest;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;

public class MemberDocumentTest extends BaseDocumentTest {

    @Nested
    class GetMember {

        RestDocsRequest requestDocument = request()
                .tag(Tag.MEMBER_API)
                .summary("회원 정보 조회")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("id").type(NUMBER).description("회원 식별자"),
                        fieldWithPath("email").type(STRING).description("회원 이메일"),
                        fieldWithPath("isSignUp").type(BOOLEAN).description("회원 가입 요청 여부 (false 고정)"),
                        fieldWithPath("nickname").type(STRING).description("회원 닉네임").optional(),
                        fieldWithPath("phoneNumber").type(STRING).description("회원 전화번호 ex) 01012345678").optional(),
                        fieldWithPath("optInMarketing").type(BOOLEAN).description("마케팅 동의 여부").optional()
                );

        @Test
        void 회원_정보_조회_성공() {
            MemberResponse response = new MemberResponse(1L, "abc@kakao.com", false, "test-nickname", "01012345678", true);
            doReturn(response).when(memberService).getMember(anyLong());

            var document = document("member/get", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/member")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class,
                names = {"UNAUTHORIZED_MEMBER", "EXPIRED_TOKEN", "INVALID_MEMBER_ID"})
        @ParameterizedTest
        void 회원_정보_조회_실패(BusinessErrorCode errorCode) {
            doThrow(new BusinessException(errorCode)).when(memberService).getMember(anyLong());

            var document = document("member/get", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/member")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class CheckNickname {

        RestDocsRequest requestDocument = request()
                .tag(Tag.MEMBER_API)
                .summary("닉네임 중복 검사")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ).queryParameter(
                        parameterWithName("nickname").description("검사할 닉네임")
                );

        @Test
        void 중복_닉네임_확인_성공() {
            doNothing().when(memberService).validateNickname(anyString(), anyLong());

            var document = document("member/nickname-check", 204)
                    .request(requestDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("nickname", "new-nickname")
                    .when().get("/api/member/nickname/check")
                    .then().statusCode(204);
        }

        @EnumSource(value = BusinessErrorCode.class,
                names = {"UNAUTHORIZED_MEMBER", "EXPIRED_TOKEN", "DUPLICATE_NICKNAME"})
        @ParameterizedTest
        void 중복_닉네임_확인_실패(BusinessErrorCode errorCode) {
            doThrow(new BusinessException(errorCode))
                    .when(memberService).validateNickname(anyString(), anyLong());

            var document = document("member/nickname-check", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("nickname", "existing-nickname")
                    .when().get("/api/member/nickname/check")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class CheckPhoneNumber {

        RestDocsRequest requestDocument = request()
                .tag(Tag.MEMBER_API)
                .summary("전화번호 중복 검사")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ).queryParameter(
                        parameterWithName("phoneNumber").description("검사할 전화번호 ex) 01012345678")
                );

        @Test
        void 중복_전화번호_확인_성공() {
            doNothing().when(memberService).validatePhoneNumber(anyString(), anyLong());

            var document = document("member/phone-number-check", 204)
                    .request(requestDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("phoneNumber", "01098765432")
                    .when().get("/api/member/phone-number/check")
                    .then().statusCode(204);
        }

        @EnumSource(value = BusinessErrorCode.class,
                names = {"UNAUTHORIZED_MEMBER", "EXPIRED_TOKEN", "DUPLICATE_PHONE_NUMBER"})
        @ParameterizedTest
        void 중복_전화번호_확인_실패(BusinessErrorCode errorCode) {
            doThrow(new BusinessException(errorCode))
                    .when(memberService).validatePhoneNumber(anyString(), anyLong());

            var document = document("member/phone-number-check", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("phoneNumber", "01012345678")
                    .when().get("/api/member/phone-number/check")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class UpdateMember {

        RestDocsRequest requestDocument = request()
                .tag(Tag.MEMBER_API)
                .summary("회원 정보 수정")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ).requestBodyField(
                        fieldWithPath("nickname").type(STRING).description("회원 닉네임"),
                        fieldWithPath("phoneNumber").type(STRING).description("회원 전화번호 ex) 01012345678"),
                        fieldWithPath("optInMarketing").type(BOOLEAN).description("마케팅 동의 여부")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("id").type(NUMBER).description("회원 식별자"),
                        fieldWithPath("email").type(STRING).description("회원 이메일"),
                        fieldWithPath("isSignUp").type(BOOLEAN).description("회원 가입 요청 여부 (false 고정)"),
                        fieldWithPath("nickname").type(STRING).description("회원 닉네임").optional(),
                        fieldWithPath("phoneNumber").type(STRING).description("회원 전화번호 ex) 01012345678").optional(),
                        fieldWithPath("optInMarketing").type(BOOLEAN).description("마케팅 동의 여부").optional()
                );

        @Test
        void 회원_정보_수정_성공() {
            MemberUpdateRequest request = new MemberUpdateRequest("update-nickname", "01012345678", true);
            MemberResponse response = new MemberResponse(1L, "abc@kakao.com", false, "update-nickname", "01012345678", true);
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

        @EnumSource(value = BusinessErrorCode.class,
                names = {"UNAUTHORIZED_MEMBER", "EXPIRED_TOKEN", "DUPLICATE_NICKNAME", "DUPLICATE_PHONE_NUMBER",
                        "INVALID_MOBILE_PHONE_NUMBER", "INVALID_MARKETING_CONSENT"})
        @ParameterizedTest
        void 회원_정보_수정_실패(BusinessErrorCode errorCode) {
            MemberUpdateRequest request = new MemberUpdateRequest("update-nickname", "01012345678", true);
            doThrow(new BusinessException(errorCode)).when(memberService).update(anyLong(), eq(request));

            var document = document("member/update", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .body(request)
                    .when().put("/api/member")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }
}
