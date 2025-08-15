package eatda.document.store;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import eatda.controller.store.ImagesResponse;
import eatda.controller.store.StoreInMemberResponse;
import eatda.controller.store.StorePreviewResponse;
import eatda.controller.store.StoreResponse;
import eatda.controller.store.StoresInMemberResponse;
import eatda.controller.store.StoresResponse;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import eatda.domain.store.District;
import eatda.domain.store.StoreCategory;
import eatda.domain.store.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;

public class StoreDocumentTest extends BaseDocumentTest {

    @Nested
    class GetStore {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("음식점 정보 조회")
                .pathParameter(
                        parameterWithName("storeId").description("음식점 ID")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("id").type(NUMBER).description("음식점 ID"),
                        fieldWithPath("kakaoId").type(STRING).description("카카오 음식점 ID"),
                        fieldWithPath("name").type(STRING).description("음식점 이름"),
                        fieldWithPath("district").type(STRING).description("음식점 주소 (구)"),
                        fieldWithPath("neighborhood").type(STRING).description("음식점 주소 (동)"),
                        fieldWithPath("category").type(STRING).description("음식점 카테고리"),
                        fieldWithPath("placeUrl").type(STRING).description("음식점 카카오맵 URL")
                );

        @Test
        void 음식점_정보를_조회() {
            StoreResponse response = new StoreResponse(1L, "17163273", "농민백암순대", "강남구", "대치동",
                    "한식", "https://place.map.kakao.com/17163273");
            doReturn(response).when(storeService).getStore(anyLong());

            long storeId = 1L;
            var document = document("store/get-store", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .pathParam("storeId", storeId)
                    .when().get("/api/shops/{storeId}")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"STORE_NOT_FOUND"})
        @ParameterizedTest
        void 음식점_정보_조회_실패(BusinessErrorCode errorCode) {
            doThrow(new BusinessException(errorCode)).when(storeService).getStore(anyLong());

            long storeId = 1L;
            var document = document("store/get-store", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .pathParam("storeId", storeId)
                    .when().get("/api/shops/{storeId}")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class GetStores {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("음식점 목록 조회")
                .queryParameter(
                        parameterWithName("page").description("조회할 음식점 페이지 (기본 값 0,시작 값 0)").optional(),
                        parameterWithName("size").description("조회할 음식점 개수 (기본 값 5, 최소 1, 최대 50)").optional(),
                        parameterWithName("category")
                                .description("음식점 카테고리(기본값: 전체) (한식,중식,일식,양식,카페/디저트,기타)").optional()
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("stores").type(ARRAY).description("음식점 목록"),
                        fieldWithPath("stores[].id").type(NUMBER).description("음식점 ID"),
                        fieldWithPath("stores[].imageUrl").type(STRING).description("음식점 대표 이미지 URL"),
                        fieldWithPath("stores[].name").type(STRING).description("음식점 이름"),
                        fieldWithPath("stores[].district").type(STRING).description("음식점 주소 (구)"),
                        fieldWithPath("stores[].neighborhood").type(STRING).description("음식점 주소 (동)"),
                        fieldWithPath("stores[].category").type(STRING).description("음식점 카테고리")
                );

        @Test
        void 음식점_목록_최신순으로_조회() {
            int page = 0;
            int size = 2;
            StoreCategory category = StoreCategory.CAFE;
            StoresResponse response = new StoresResponse(List.of(
                    new StorePreviewResponse(2L, "https://example.image", "농민백암순대", "강남구", "대치동", "한식"),
                    new StorePreviewResponse(1L, "https://example.image", "석관동떡볶이", "성북구", "석관동", "한식")
            ));
            doReturn(response).when(storeService).getStores(page, size, category.getCategoryName());

            var document = document("store/get", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .queryParam("category", category.getCategoryName())
                    .when().get("/api/shops")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"PRESIGNED_URL_GENERATION_FAILED"})
        @ParameterizedTest
        void 음식점_목록_조회_실패(BusinessErrorCode errorCode) {
            int page = 0;
            int size = 2;
            StoreCategory category = StoreCategory.CAFE;
            doThrow(new BusinessException(errorCode))
                    .when(storeService).getStores(page, size, category.getCategoryName());

            var document = document("store/get", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .queryParam("category", category.getCategoryName())
                    .when().get("/api/shops")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class GetStoreImages {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("가게 이미지 조회")
                .pathParameter(
                        parameterWithName("storeId").description("음식점 ID")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("imageUrls").type(ARRAY).description("음식점 이미지 URL 목록")
                );

        @Test
        void 음식점_이미지들을_조회() {
            long storeId = 1L;
            ImagesResponse response = new ImagesResponse(List.of(
                    "https://example.image/1.jpg",
                    "https://example.image/2.jpg",
                    "https://example.image/3.jpg"
            ));
            doReturn(response).when(storeService).getStoreImages(storeId);

            var document = document("store/get-images", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .pathParam("storeId", storeId)
                    .when().get("/api/shops/{storeId}/images")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"STORE_NOT_FOUND"})
        @ParameterizedTest
        void 음식점_이미지_조회_실패(BusinessErrorCode errorCode) {
            long storeId = 1L;
            doThrow(new BusinessException(errorCode)).when(storeService).getStoreImages(storeId);

            var document = document("get-images", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .pathParam("storeId", storeId)
                    .when().get("/api/shops/{storeId}/images")
                    .then().statusCode(errorCode.getStatus().value());
        }

    }

    @Nested
    class GetStoresByCheeredMember {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("회원이 응원한 가게 목록 조회")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("stores").type(ARRAY).description("응원한 음식점 목록"),
                        fieldWithPath("stores[].id").type(NUMBER).description("음식점 ID"),
                        fieldWithPath("stores[].name").type(STRING).description("음식점 이름"),
                        fieldWithPath("stores[].district").type(STRING).description("음식점 주소 (구)"),
                        fieldWithPath("stores[].neighborhood").type(STRING).description("음식점 주소 (동)"),
                        fieldWithPath("stores[].cheerCount").type(NUMBER).description("해당 음식점 응원 횟수 (자신 포함)")
                );

        @Test
        void 회원이_응원한_음식점_목록을_조회() {
            StoresInMemberResponse response = new StoresInMemberResponse(List.of(
                    new StoreInMemberResponse(1L, "농민백암순대", "강남구", "대치동", 5L),
                    new StoreInMemberResponse(2L, "홍콩반점", "강남구", "역삼동", 1L)
            ));
            doReturn(response).when(storeService).getStoresByCheeredMember(anyLong());

            var document = document("store/get-by-cheered-member", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/shops/cheered-member")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"UNAUTHORIZED_MEMBER", "EXPIRED_TOKEN", "INVALID_MEMBER_ID"})
        @ParameterizedTest
        void 회원이_응원한_음식점_목록_조회_실패(BusinessErrorCode errorCode) {
            doThrow(new BusinessException(errorCode)).when(storeService).getStoresByCheeredMember(anyLong());

            var document = document("store/get-by-cheered-member", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/shops/cheered-member")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class SearchStores {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("음식점 검색")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ).queryParameter(
                        parameterWithName("query").description("검색어")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("stores").type(ARRAY).description("음식점 검색 결과"),
                        fieldWithPath("stores[].kakaoId").type(STRING).description("카카오 음식점 ID"),
                        fieldWithPath("stores[].name").type(STRING).description("음식점 이름"),
                        fieldWithPath("stores[].address").type(STRING).description("음식점 주소 (지번 주소)")
                );

        @Test
        void 음식점_검색_성공() {
            String query = "농민백암순대";
            List<StoreSearchResult> responses = List.of(
                    new StoreSearchResult("123", StoreCategory.KOREAN, "010-1234-1234", "농민백암순대 본점",
                            "https://yap.co.kr", "서울 강남구 대치동 896-33", "서울 강남구 선릉로86길 40-4", District.GANGNAM,
                            37.0d, 128.0d),
                    new StoreSearchResult("456", StoreCategory.KOREAN, "010-1234-1234", "농민백암순대 시청점",
                            "https://yapp.kr", "서울 중구 북창동 19-4", null, District.JUNG, 37.0d, 128.0d)
            );
            doReturn(responses).when(storeSearchService).searchStores(anyString());

            var document = document("store/search", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("query", query)
                    .when().get("/api/shop/search")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class,
                names = {"UNAUTHORIZED_MEMBER", "EXPIRED_TOKEN", "MAP_SERVER_ERROR"})
        @ParameterizedTest
        void 음식점_검색_실패(BusinessErrorCode errorCode) {
            String query = "농민백암순대";
            doThrow(new BusinessException(errorCode)).when(storeSearchService).searchStores(anyString());

            var document = document("store/search", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("query", query)
                    .when().get("/api/shop/search")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }
}
