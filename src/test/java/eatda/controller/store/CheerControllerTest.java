package eatda.controller.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.BaseControllerTest;
import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import eatda.util.ImageUtils;
import eatda.util.MappingUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class CheerControllerTest extends BaseControllerTest {

    @Nested
    class RegisterCheer {

        @Test
        void 응원을_등록한다() {
            Store store = storeGenerator.generate("123", "서울시 노원구 월계3동 123-45");
            CheerRegisterRequest request = new CheerRegisterRequest(store.getKakaoId(), store.getName(), "맛있어요!");

            CheerResponse response = given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType("multipart/form-data")
                    .multiPart("request", "request.json", MappingUtils.toJsonBytes(request), "application/json")
                    .multiPart("image", ImageUtils.getTestImage(), "image/png")
                    .when()
                    .post("/api/cheer")
                    .then()
                    .statusCode(201)
                    .extract().as(CheerResponse.class);

            assertThat(response.storeId()).isEqualTo(store.getId());
        }

        @Test
        void 이미지가_비어있을_경우에도_응원을_등록한다() {
            Store store = storeGenerator.generate("123", "서울시 노원구 월계3동 123-45");
            CheerRegisterRequest request = new CheerRegisterRequest(store.getKakaoId(), store.getName(), "맛있어요!");

            CheerResponse response = given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType("multipart/form-data")
                    .multiPart("request", "request.json", MappingUtils.toJsonBytes(request), "application/json")
                    .when()
                    .post("/api/cheer")
                    .then()
                    .statusCode(201)
                    .extract().as(CheerResponse.class);

            assertThat(response.storeId()).isEqualTo(store.getId());
        }
    }

    @Nested
    class GetCheers {

        @Test
        void 요청한_응원_중_최신_응원_N개를_조회한다() {
            Member member = memberGenerator.generateRegisteredMember("nickname", "ac@kakao.com", "123", "01011111111");
            Store store1 = storeGenerator.generate("111", "서울시 노원구 월계3동 123-45");
            Store store2 = storeGenerator.generate("222", "서울시 성북구 석관동 123-45");
            Cheer cheer1 = cheerGenerator.generateAdmin(member, store1);
            Cheer cheer2 = cheerGenerator.generateAdmin(member, store1);
            Cheer cheer3 = cheerGenerator.generateAdmin(member, store2);

            CheersResponse response = given()
                    .when()
                    .queryParam("size", 2)
                    .get("/api/cheer")
                    .then()
                    .statusCode(200)
                    .extract().as(CheersResponse.class);

            CheerPreviewResponse firstResponse = response.cheers().get(0);
            assertAll(
                    () -> assertThat(response.cheers()).hasSize(2),
                    () -> assertThat(firstResponse.storeId()).isEqualTo(store2.getId()),
                    () -> assertThat(firstResponse.storeDistrict()).isEqualTo("성북구"),
                    () -> assertThat(firstResponse.storeNeighborhood()).isEqualTo("석관동"),
                    () -> assertThat(firstResponse.cheerId()).isEqualTo(cheer3.getId())
            );
        }

        @Nested
        class GetCheersByStoreId {

            @Test
            void 가게_아이디로_응원을_조회한다() throws InterruptedException {
                Member member1 = memberGenerator.generateRegisteredMember("123", "a@gmail.com", "1234", "01012341234");
                Member member2 = memberGenerator.generateRegisteredMember("124", "b@gmail.com", "1235", "01012341235");
                Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
                Cheer cheer1 = cheerGenerator.generateCommon(member1, store);
                Thread.sleep(5);
                Cheer cheer2 = cheerGenerator.generateCommon(member2, store);

                CheersInStoreResponse response = given()
                        .when()
                        .queryParam("size", 2)
                        .get("/api/shops/{storeId}/cheers", store.getId())
                        .then()
                        .statusCode(200)
                        .extract().as(CheersInStoreResponse.class);

                assertAll(
                        () -> assertThat(response.cheers()).hasSize(2),
                        () -> assertThat(response.cheers().get(0).id()).isEqualTo(cheer2.getId()),
                        () -> assertThat(response.cheers().get(1).id()).isEqualTo(cheer1.getId())
                );
            }
        }
    }
}
