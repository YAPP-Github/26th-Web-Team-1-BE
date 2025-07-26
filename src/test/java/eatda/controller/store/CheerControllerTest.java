package eatda.controller.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.BaseControllerTest;
import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import eatda.util.ImageUtils;
import eatda.util.MappingUtils;
import java.time.LocalDateTime;
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
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Cheer cheer1 = cheerGenerator.generateAdmin(member, store1, startAt);
            Cheer cheer2 = cheerGenerator.generateAdmin(member, store1, startAt.plusHours(1));
            Cheer cheer3 = cheerGenerator.generateAdmin(member, store2, startAt.plusHours(2));

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
    }
}
