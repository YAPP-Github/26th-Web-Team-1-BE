package eatda.document.store;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;

import eatda.controller.store.CheerInStoreResponse;
import eatda.controller.store.CheerPreviewResponse;
import eatda.controller.store.CheerRegisterRequest;
import eatda.controller.store.CheerResponse;
import eatda.controller.store.CheersInStoreResponse;
import eatda.controller.store.CheersResponse;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.util.ImageUtils;
import eatda.util.MappingUtils;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;

public class CheerDocumentTest extends BaseDocumentTest {

    @Nested
    class RegisterCheer {

        private static final String REQUEST_DESCRIPTION_MARKDOWN = """
                - 요청 형식 : multipart/form-data
                - 요청 field
                  - `image` : 응원 이미지 (선택, 최대 5MB, 허용 타입 : image/jpg, image/jpeg, image/png
                  - `request` : 응원 등록 요청 정보 (필수, 허용 타입 : application/json)
                - request body 예시
                    ```json
                    {
                        "storeKakaoId": "123", // 가게 카카오 ID
                        "storeName": "농민백암순대 본점", // 가게 이름
                        "description": "너무 맛있어요! 준환님 추천 맛집!" // 응원 내용
                    }
                    ```
                """;

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("응원 등록")
                .description(REQUEST_DESCRIPTION_MARKDOWN)
                .requestHeader(
                        headerWithName("Authorization").description("인증 토큰")
                ).multipartField(
                        partWithName("image").description("응원 이미지 (선택)").optional(),
                        partWithName("request").description("응원 등록 요청 정보")
                ).requestBodyField("request",
                        fieldWithPath("storeKakaoId").type(STRING).description("가게 카카오 ID"),
                        fieldWithPath("storeName").type(STRING).description("가게 이름"),
                        fieldWithPath("description").type(STRING).description("응원 내용")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("storeId").type(NUMBER).description("가게 ID"),
                        fieldWithPath("cheerId").type(NUMBER).description("응원 ID"),
                        fieldWithPath("imageUrl").type(STRING).description("이미지 URL").optional(),
                        fieldWithPath("cheerDescription").type(STRING).description("응원 내용")
                );

        @Test
        void 응원_등록_성공() {
            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "너무 맛있어요!");
            CheerResponse response = new CheerResponse(1L, 1L, "https://example.img", "너무 맛있어요!");
            doReturn(response).when(cheerService).registerCheer(eq(request), any(), anyLong());

            var document = document("cheer/register", 201)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType("multipart/form-data")
                    .multiPart("request", "request.json", MappingUtils.toJsonBytes(request), "application/json")
                    .multiPart("image", ImageUtils.getTestImage(), "image/png")
                    .when().post("/api/cheer")
                    .then().statusCode(201);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {
                "UNAUTHORIZED_MEMBER",
                "EXPIRED_TOKEN",
                "FULL_CHEER_SIZE_PER_MEMBER",
                "MAP_SERVER_ERROR",
                "STORE_NOT_FOUND",
                "FILE_UPLOAD_FAILED",
                "INVALID_IMAGE_TYPE",
                "PRESIGNED_URL_GENERATION_FAILED",
                "INVALID_CHEER_DESCRIPTION"})
        @ParameterizedTest
        void 응원_등록_실패(BusinessErrorCode errorCode) {
            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "너무 맛있어요!");
            doThrow(new BusinessException(errorCode)).when(cheerService).registerCheer(eq(request), any(), anyLong());

            var document = document("cheer/register", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType(ContentType.MULTIPART)
                    .multiPart("request", "request.json", MappingUtils.toJsonBytes(request), "application/json")
                    .multiPart("image", ImageUtils.getTestImage(), "image/png")
                    .when().post("/api/cheer")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class GetCheers {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("최신 응원 검색")
                .queryParameter(
                        parameterWithName("size").description("조회 개수 (최소 1, 최대 50)")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("cheers").type(ARRAY).description("응원 검색 결과"),
                        fieldWithPath("cheers[].storeId").type(NUMBER).description("가게 ID"),
                        fieldWithPath("cheers[].imageUrl").type(STRING).description("이미지 URL").optional(),
                        fieldWithPath("cheers[].storeName").type(STRING).description("가게 이름"),
                        fieldWithPath("cheers[].storeDistrict").type(STRING).description("가게 주소 (구)"),
                        fieldWithPath("cheers[].storeNeighborhood").type(STRING).description("가게 주소 (동)"),
                        fieldWithPath("cheers[].storeCategory").type(STRING).description("가게 카테고리"),
                        fieldWithPath("cheers[].cheerId").type(NUMBER).description("응원 ID"),
                        fieldWithPath("cheers[].cheerDescription").type(STRING).description("응원 내용")
                );

        @Test
        void 음식점_검색_성공() {
            int size = 2;
            CheersResponse responses = new CheersResponse(List.of(
                    new CheerPreviewResponse(2L, "https://example.image", "농민백암순대 본점", "강남구", "선릉구", "한식", 2L,
                            "너무 맛있어요!"),
                    new CheerPreviewResponse(1L, null, "석관동떡볶이", "성북구", "석관동", "기타", 1L,
                            "너무 매워요! 하지만 맛있어요!")
            ));
            doReturn(responses).when(cheerService).getCheers(anyInt());

            var document = document("cheer/get-many", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("size", size)
                    .when().get("/api/cheer")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"PRESIGNED_URL_GENERATION_FAILED"})
        @ParameterizedTest
        void 음식점_검색_실패(BusinessErrorCode errorCode) {
            int size = 2;
            doThrow(new BusinessException(errorCode)).when(cheerService).getCheers(anyInt());

            var document = document("cheer/get-many", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("size", size)
                    .when().get("/api/cheer")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class GetCheersByStoreId {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("가게별 응원 검색")
                .pathParameter(
                        parameterWithName("storeId").description("가게 ID")
                )
                .queryParameter(
                        parameterWithName("size").description("조회 개수 (최소 1, 최대 50)")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("cheers").type(ARRAY).description("응원 검색 결과"),
                        fieldWithPath("cheers[].id").type(NUMBER).description("응원 ID"),
                        fieldWithPath("cheers[].memberId").type(NUMBER).description("응원 작성자 회원 ID"),
                        fieldWithPath("cheers[].memberNickname").type(STRING).description("응원 작성자 닉네임"),
                        fieldWithPath("cheers[].description").type(STRING).description("응원 내용")
                );

        @Test
        void 가게별_응원_검색_성공() {
            Long storeId = 1L;
            int size = 2;
            CheersInStoreResponse responses = new CheersInStoreResponse(List.of(
                    new CheerInStoreResponse(20L, 5L, "커찬", "너무 맛있어요!"),
                    new CheerInStoreResponse(10L, 3L, "찬커", "너무 매워요! 하지만 맛있어요!")
            ));
            doReturn(responses).when(cheerService).getCheersByStoreId(storeId, size);

            var document = document("cheer/get-store-id", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("size", size)
                    .when().get("/api/shops/{storeId}/cheers", storeId)
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"STORE_NOT_FOUND"})
        @ParameterizedTest
        void 가게별_응원_검색_실패(BusinessErrorCode errorCode) {
            Long storeId = 1L;
            int size = 2;
            doThrow(new BusinessException(errorCode)).when(cheerService).getCheersByStoreId(eq(storeId), anyInt());

            var document = document("cheer/get-store-id", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("size", size)
                    .when().get("/api/shops/{storeId}/cheers", storeId)
                    .then().statusCode(errorCode.getStatus().value());
        }
    }
}
