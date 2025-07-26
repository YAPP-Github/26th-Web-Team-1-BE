package eatda.service.store;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import eatda.client.map.MapClient;
import eatda.client.map.StoreSearchResult;
import eatda.controller.store.ImagesResponse;
import eatda.controller.store.StorePreviewResponse;
import eatda.controller.store.StoreSearchResponses;
import eatda.controller.store.StoresResponse;
import eatda.domain.store.Store;
import eatda.repository.store.CheerRepository;
import eatda.repository.store.StoreRepository;
import eatda.storage.image.ImageStorage;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final MapClient mapClient;
    private final StoreSearchFilter storeSearchFilter;
    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;
    private final ImageStorage imageStorage;

    public ImagesResponse getStoreImages(long storeId) {
        Store store = storeRepository.getById(storeId);
        List<String> imageUrls = cheerRepository.findAllImageKey(store)
                .stream()
                .map(imageStorage::getPreSignedUrl)
                .toList();
        return new ImagesResponse(imageUrls);
    }

    // TODO : N+1 문제 해결
    public StoresResponse getStores(int size) {
        return storeRepository.findAllByOrderByCreatedAtDesc(Pageable.ofSize(size))
                .stream()
                .map(store -> new StorePreviewResponse(store, getStoreImageUrl(store).orElse(null)))
                .collect(collectingAndThen(toList(), StoresResponse::new));
    }

    private Optional<String> getStoreImageUrl(Store store) {
        return cheerRepository.findRecentImageKey(store)
                .map(imageStorage::getPreSignedUrl);
    }

    public StoreSearchResponses searchStores(String query) {
        List<StoreSearchResult> searchResults = mapClient.searchShops(query);
        List<StoreSearchResult> filteredResults = storeSearchFilter.filterSearchedStores(searchResults);
        return StoreSearchResponses.from(filteredResults);
    }
}
