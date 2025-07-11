package eatda.service.store;

import eatda.client.map.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StoreSearchFilter {

    public List<StoreSearchResult> filterSearchedStores(List<StoreSearchResult> searchResults) {
        return searchResults.stream()
                .filter(this::isValidStore)
                .toList();
    }

    public StoreSearchResult findStore(List<StoreSearchResult> searchResults, String kakaoId) {
        return searchResults.stream()
                .filter(this::isValidStore)
                .filter(result -> result.kakaoId().equals(kakaoId))
                .findAny()
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORE_SEARCH_FAILED));
    }

    private boolean isValidStore(StoreSearchResult store) {
        return store.isFoodStore() && store.isInSeoul();
    }
}
