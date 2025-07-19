package eatda.service.story;

import eatda.client.map.StoreSearchResult;
import eatda.controller.story.FilteredSearchResult;
import eatda.controller.story.StoriesResponse;
import eatda.controller.story.StoryRegisterRequest;
import eatda.controller.story.StoryResponse;
import eatda.domain.member.Member;
import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.image.ImageDomain;
import eatda.repository.image.ImageRepository;
import eatda.repository.member.MemberRepository;
import eatda.repository.story.StoryRepository;
import eatda.service.store.StoreService;
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

    private final StoreService storeService;
    private final ImageRepository imageRepository;
    private final StoryRepository storyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void registerStory(StoryRegisterRequest request, MultipartFile image, Long memberId) {
        Member member = memberRepository.getById(memberId);
        List<StoreSearchResult> searchResponses = storeService.searchStoreResults(request.query());
        FilteredSearchResult matchedStore = filteredSearchResponse(searchResponses, request.storeKakaoId());
        String imageKey = imageRepository.upload(image, ImageDomain.STORY);

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
                        store.categoryName()
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
                                imageRepository.getPresignedUrl(story.getImageKey())
                        ))
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public StoryResponse getStory(long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORY_NOT_FOUND));

        return new StoryResponse(
                story.getStoreKakaoId(),
                story.getStoreCategory(),
                story.getStoreName(),
                story.getAddressDistrict(),
                story.getAddressNeighborhood(),
                story.getDescription(),
                imageRepository.getPresignedUrl(story.getImageKey())
        );
    }
}
