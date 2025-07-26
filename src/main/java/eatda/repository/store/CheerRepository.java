package eatda.repository.store;

import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface CheerRepository extends Repository<Cheer, Long> {

    Cheer save(Cheer cheer);

    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Cheer> findAllByStoreOrderByCreatedAtDesc(Store store, Pageable pageable);

    @Query("""
            SELECT c.imageKey FROM Cheer c
                WHERE c.store = :store AND c.imageKey IS NOT NULL
                ORDER BY c.createdAt DESC
                LIMIT 1""")
    Optional<ImageKey> findRecentImageKey(Store store);

    int countByMember(Member member);

    boolean existsByMemberAndStoreKakaoId(Member member, String storeKakaoId);
}
