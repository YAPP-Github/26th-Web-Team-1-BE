package eatda.service.story;

import eatda.client.map.MapClient;
import eatda.client.map.StoreSearchResult;
import eatda.controller.story.FilteredSearchResult;
import eatda.controller.story.StoriesDetailResponse;
import eatda.controller.story.StoriesResponse;
import eatda.controller.story.StoryRegisterRequest;
import eatda.controller.story.StoryRegisterResponse;
import eatda.controller.story.StoryResponse;
import eatda.domain.Image;
import eatda.domain.ImageDomain;
import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.StoreRepository;
import eatda.repository.story.StoryRepository;
import eatda.storage.image.ImageStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StoryService {

    private static final int PAGE_START_NUMBER = 0;

    private final ImageStorage imageStorage;
    private final MapClient mapClient;
    private final StoryRepository storyRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public StoryRegisterResponse registerStory(StoryRegisterRequest request, MultipartFile imageFile, Long memberId) {
        Member member = memberRepository.getById(memberId);
        List<StoreSearchResult> searchResponses = mapClient.searchShops(request.storeName());
        FilteredSearchResult matchedStore = filteredSearchResponse(searchResponses, request.storeKakaoId());
        ImageKey imageKey = imageStorage.upload(new Image(ImageDomain.STORY, imageFile));

        Story story = Story.builder()
                .member(member)
                .storeKakaoId(matchedStore.kakaoId())
                .storeName(matchedStore.name())
                .storeRoadAddress(matchedStore.roadAddress())
                .storeLotNumberAddress(matchedStore.lotNumberAddress())
                .storeCategory(matchedStore.category())
                .description(request.description())
                .imageKey(imageKey)
                .build();

        storyRepository.save(story);

        return new StoryRegisterResponse(story.getId());
    }

    private FilteredSearchResult filteredSearchResponse(List<StoreSearchResult> responses, String storeKakaoId) {
        return responses.stream()
                .filter(store -> store.kakaoId().equals(storeKakaoId))
                .findFirst()
                .map(store -> new FilteredSearchResult(
                        store.kakaoId(),
                        store.name(),
                        store.roadAddress(),
                        store.lotNumberAddress(),
                        store.getStoreCategory()
                ))
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public StoriesResponse getPagedStoryPreviews(int size) {
        Pageable pageable = PageRequest.of(PAGE_START_NUMBER, size);
        Page<Story> orderByPage = storyRepository.findAllByOrderByCreatedAtDesc(pageable);

        return new StoriesResponse(
                orderByPage.getContent().stream()
                        .map(story -> new StoriesResponse.StoryPreview(
                                story.getId(),
                                imageStorage.getPreSignedUrl(story.getImageKey())
                        ))
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public StoryResponse getStory(long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORY_NOT_FOUND));
        Long storeId = storeRepository.findByKakaoId(story.getStoreKakaoId())
                .map(Store::getId)
                .orElse(null);

        return new StoryResponse(
                storeId,
                story.getStoreKakaoId(),
                story.getStoreCategory().getCategoryName(),
                story.getStoreName(),
                story.getAddressDistrict(),
                story.getAddressNeighborhood(),
                story.getDescription(),
                imageStorage.getPreSignedUrl(story.getImageKey()),
                story.getMember().getId(),
                story.getMember().getNickname()
        );
    }

    @Transactional(readOnly = true)
    public StoriesDetailResponse getPagedStoryDetails(String kakaoId, int size) {
        List<Story> stories = storyRepository
                .findAllByStoreKakaoIdOrderByCreatedAtDesc(kakaoId, PageRequest.of(PAGE_START_NUMBER, size))
                .getContent();

        List<StoriesDetailResponse.StoryDetailResponse> responses = stories.stream()
                .map(story -> new StoriesDetailResponse.StoryDetailResponse(
                        story, imageStorage.getPreSignedUrl(story.getImageKey())))
                .toList(); // TODO: N+1 문제 해결
        return new StoriesDetailResponse(responses);
    }
}
