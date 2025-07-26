package eatda.controller.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.BaseControllerTest;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class StoreControllerTest extends BaseControllerTest {

    @Nested
    class GetStores {

        @Test
        void 음식점_목록을_최신순으로_조회한다() {
            Member member = memberGenerator.generate("111");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Store store1 = storeGenerator.generate("111", "서울 강남구 대치동 896-33", startAt);
            Store store2 = storeGenerator.generate("222", "서울 강남구 대치동 896-34", startAt.plusHours(1));
            Store store3 = storeGenerator.generate("333", "서울 강남구 대치동 896-35", startAt.plusHours(2));
            cheerGenerator.generateCommon(member, store1, "image-key-1");
            cheerGenerator.generateCommon(member, store2, "image-key-2");
            cheerGenerator.generateCommon(member, store3, "image-key-3");

            int size = 2;

            StoresResponse response = given()
                    .queryParam("size", size)
                    .when()
                    .get("/api/shops")
                    .then()
                    .statusCode(200)
                    .extract().as(StoresResponse.class);

            assertAll(
                    () -> assertThat(response.stores()).hasSize(size),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store3.getId()),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store2.getId())
            );
        }
    }

    @Nested
    class SearchStores {

        @Test
        void 음식점_검색_결과를_반환한다() {
            String query = "농민백암순대";

            StoreSearchResponses responses = given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("query", query)
                    .when()
                    .get("/api/shop/search")
                    .then()
                    .statusCode(200)
                    .extract().as(StoreSearchResponses.class);

            assertThat(responses.stores()).isNotEmpty();
        }
    }

}
