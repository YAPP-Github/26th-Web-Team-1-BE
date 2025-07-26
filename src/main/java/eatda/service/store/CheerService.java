package eatda.service.store;

import eatda.client.map.MapClient;
import eatda.client.map.StoreSearchResult;
import eatda.controller.store.CheerInStoreResponse;
import eatda.controller.store.CheerPreviewResponse;
import eatda.controller.store.CheerRegisterRequest;
import eatda.controller.store.CheerResponse;
import eatda.controller.store.CheersInStoreResponse;
import eatda.controller.store.CheersResponse;
import eatda.domain.Image;
import eatda.domain.ImageDomain;
import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.CheerRepository;
import eatda.repository.store.StoreRepository;
import eatda.storage.image.ImageStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CheerService {

    private static final int MAX_CHEER_SIZE = 3;

    private final MapClient mapClient;
    private final StoreSearchFilter storeSearchFilter;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;
    private final ImageStorage imageStorage;

    @Transactional
    public CheerResponse registerCheer(CheerRegisterRequest request, MultipartFile imageFile, long memberId) {
        Member member = memberRepository.getById(memberId);
        validateRegisterCheer(member, request.storeKakaoId());

        List<StoreSearchResult> searchResults = mapClient.searchShops(request.storeName());
        StoreSearchResult result = storeSearchFilter.filterStoreByKakaoId(searchResults, request.storeKakaoId());
        ImageKey imageKey = imageStorage.upload(new Image(ImageDomain.CHEER, imageFile));

        Store store = storeRepository.findByKakaoId(result.kakaoId())
                .orElseGet(() -> storeRepository.save(result.toStore())); // TODO 상점 조회/저장 동시성 이슈 해결
        Cheer cheer = cheerRepository.save(new Cheer(member, store, request.description(), imageKey));
        return new CheerResponse(cheer, imageStorage.getPreSignedUrl(imageKey), store);
    }

    private void validateRegisterCheer(Member member, String storeKakaoId) {
        if (cheerRepository.countByMember(member) >= MAX_CHEER_SIZE) {
            throw new BusinessException(BusinessErrorCode.FULL_CHEER_SIZE_PER_MEMBER);
        }
        if (cheerRepository.existsByMemberAndStoreKakaoId(member, storeKakaoId)) {
            throw new BusinessException(BusinessErrorCode.ALREADY_CHEERED);
        }
    }

    @Transactional(readOnly = true)
    public CheersResponse getCheers(int size) {
        List<Cheer> cheers = cheerRepository.findAllByOrderByCreatedAtDesc(Pageable.ofSize(size));
        return toCheersResponse(cheers);
    }

    private CheersResponse toCheersResponse(List<Cheer> cheers) {
        return new CheersResponse(cheers.stream()
                .map(cheer -> new CheerPreviewResponse(cheer, cheer.getStore(),
                        imageStorage.getPreSignedUrl(cheer.getImageKey())))
                .toList());
    }

    @Transactional(readOnly = true)
    public CheersInStoreResponse getCheersByStoreId(Long storeId, int size) {
        Store store = storeRepository.getById(storeId);
        List<Cheer> cheers = cheerRepository.findAllByStoreOrderByCreatedAtDesc(store, Pageable.ofSize(size));

        List<CheerInStoreResponse> cheersResponse = cheers.stream()
                .map(CheerInStoreResponse::new)
                .toList(); // TODO N+1 문제 해결
        return new CheersInStoreResponse(cheersResponse);
    }
}
