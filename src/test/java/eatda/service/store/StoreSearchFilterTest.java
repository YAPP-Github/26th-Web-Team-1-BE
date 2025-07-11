package eatda.service.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.client.map.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StoreSearchFilterTest {

    private final StoreSearchFilter storeSearchFilter = new StoreSearchFilter();

    @Nested
    class FilterSearchedStores {

        @Test
        void 빈_검색_결과를_넣으면_빈_리스트를_반환한다() {
            List<StoreSearchResult> actual = storeSearchFilter.filterSearchedStores(List.of());

            assertThat(actual).isEmpty();
        }

        @Test
        void 음식점이_아니거나_서울에_위치하지_않는_가게는_제외한다() {
            StoreSearchResult store1 = createStore("1", "서울음식점1", "FD6", "서울 강남구 대치동 896-33");
            StoreSearchResult store2 = createStore("2", "카페", "CD2", "서울 강남구 대치동 896-33");
            StoreSearchResult store3 = createStore("3", "서울음식점2", "FD6", "서울 강남구 대치동 896-33");
            StoreSearchResult store4 = createStore("4", "부산음식점", "FD6", "부산 연제구 연산동 632-8");

            List<StoreSearchResult> searchResults = List.of(store1, store2, store3, store4);
            List<StoreSearchResult> actual = storeSearchFilter.filterSearchedStores(searchResults);

            assertThat(actual).containsExactly(store1, store3);
        }
    }

    @Nested
    class FindStore {

        @Test
        void 가게를_찾을_수_있다() {
            StoreSearchResult store1 = createStore("123", "서울음식점1", "FD6", "서울 강남구 대치동 896-33");
            StoreSearchResult store2 = createStore("124", "서울음식점2", "FD6", "서울 강남구 대치동 896-33");
            List<StoreSearchResult> searchResults = List.of(store1, store2);

            StoreSearchResult actual = storeSearchFilter.findStore(searchResults, store1.kakaoId());

            assertThat(actual).isEqualTo(store1);
        }

        @Test
        void 존재하지_않는_가게를_찾으면_예외를_던진다() {
            StoreSearchResult store1 = createStore("123", "서울음식점1", "FD6", "서울 강남구 대치동 896-33");
            StoreSearchResult store2 = createStore("124", "서울음식점2", "FD6", "서울 강남구 대치동 896-33");
            List<StoreSearchResult> searchResults = List.of(store1, store2);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> storeSearchFilter.findStore(searchResults, "125"));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.STORE_SEARCH_FAILED);
        }
    }

    private StoreSearchResult createStore(String id, String name, String categoryGroupCode, String location) {
        return new StoreSearchResult(id, categoryGroupCode, "음식점 > 식당", "010-1234-1234", name, "https://yapp.co.kr",
                location, null, 37.0d, 128.0d);
    }
}
