package eatda.service.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import eatda.client.map.StoreSearchResult;
import eatda.controller.store.ImagesResponse;
import eatda.controller.store.StoreResponse;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.service.BaseServiceTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StoreServiceTest extends BaseServiceTest {

    @Autowired
    private StoreService storeService;

    @Nested
    class GetStore {

        @Test
        void 음식점_정보를_조회한다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store, "image-key");

            StoreResponse response = storeService.getStore(store.getId());

            assertAll(
                    () -> assertThat(response.id()).isEqualTo(store.getId()),
                    () -> assertThat(response.name()).isEqualTo(store.getName()),
                    () -> assertThat(response.district()).isEqualTo("강남구"),
                    () -> assertThat(response.neighborhood()).isEqualTo("대치동")
            );
        }

        @Test
        void 해당_음식점이_없을_경우_예외를_던진다() {
            long nonExistentStoreId = 999L;

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> storeService.getStore(nonExistentStoreId));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.STORE_NOT_FOUND);
        }
    }

    @Nested
    class GetStores {

        @Test
        void 모든_카테고리의_음식점_목록을_최신순으로_조회한다() {
            Member member = memberGenerator.generate("111");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Store store1 = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33", StoreCategory.KOREAN, startAt);
            Store store2 = storeGenerator.generate("석관동떡볶이", "서울 성북구 석관동 123-45", StoreCategory.OTHER,
                    startAt.plusHours(1));
            Store store3 = storeGenerator.generate("강남순대국", "서울 강남구 역삼동 678-90", StoreCategory.KOREAN,
                    startAt.plusHours(2));
            cheerGenerator.generateCommon(member, store1, "image-key-1");
            cheerGenerator.generateCommon(member, store2, "image-key-2");
            cheerGenerator.generateCommon(member, store3, "image-key-3");
            int size = 2;

            var response = storeService.getStores(size, null);

            assertAll(
                    () -> assertThat(response.stores()).hasSize(size),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store3.getId()),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store2.getId())
            );
        }

        @Test
        void 특정_카테고리의_음식점_목록을_최신순으로_조회한다() {
            Member member = memberGenerator.generate("111");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Store store1 = storeGenerator.generate("112", "서울 강남구 대치동 896-33", StoreCategory.KOREAN, startAt);
            Store store2 = storeGenerator.generate("113", "서울 성북구 석관동 123-45", StoreCategory.OTHER,
                    startAt.plusHours(1));
            Store store3 = storeGenerator.generate("114", "서울 강남구 역삼동 678-90", StoreCategory.KOREAN,
                    startAt.plusHours(2));
            cheerGenerator.generateCommon(member, store1, "image-key-1");
            cheerGenerator.generateCommon(member, store2, "image-key-2");
            cheerGenerator.generateCommon(member, store3, "image-key-3");
            int size = 2;
            StoreCategory category = StoreCategory.KOREAN;

            var response = storeService.getStores(size, category.getCategoryName());

            assertAll(
                    () -> assertThat(response.stores()).hasSize(size),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store3.getId()),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store1.getId())
            );
        }
    }

    @Nested
    class GetStoreImages {

        @Test
        void 음식점_이미지들을_조회한다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store, "image-key-1");
            cheerGenerator.generateCommon(member, store, "image-key-2");
            cheerGenerator.generateCommon(member, store, "image-key-3");

            ImagesResponse response = storeService.getStoreImages(store.getId());

            assertThat(response.imageUrls()).hasSize(3);
        }

        @Test
        void 음식점_이미지가_없다면_빈_리스트를_반환한다() {
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");

            ImagesResponse response = storeService.getStoreImages(store.getId());

            assertThat(response.imageUrls()).isEmpty();
        }

        @Test
        void 음식점이_존재하지_않으면_예외를_발생시킨다() {
            long nonExistentStoreId = 999L;

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> storeService.getStoreImages(nonExistentStoreId));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.STORE_NOT_FOUND);
        }
    }

    @Nested
    class SearchStores {

        @Test
        void 음식점_검색_결과를_반환한다() {
            mockingMapClient();
            String query = "농민백암순대";

            var response = storeService.searchStores(query);

            assertAll(
                    () -> assertThat(response.stores()).hasSize(2),
                    () -> assertThat(response.stores().get(0).kakaoId()).isEqualTo("123"),
                    () -> assertThat(response.stores().get(0).address()).isEqualTo("서울 강남구 대치동 896-33"),
                    () -> assertThat(response.stores().get(1).kakaoId()).isEqualTo("456"),
                    () -> assertThat(response.stores().get(1).address()).isEqualTo("서울 중구 북창동 19-4")
            );
        }
    }

    void mockingMapClient() {
        List<StoreSearchResult> searchResults = List.of(
                new StoreSearchResult("123", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 본점", "https://yapp.co.kr",
                        "서울 강남구 대치동 896-33", "서울 강남구 선릉로86길 40-4", 37.0d, 128.0d),
                new StoreSearchResult("456", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 시청점", "http://yapp.kr",
                        "서울 중구 북창동 19-4", null, 37.0d, 128.0d)
        );

        doReturn(searchResults).when(mapClient).searchShops(anyString());
    }
}
