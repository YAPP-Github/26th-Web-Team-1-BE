package eatda.repository.store;

import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheerRepository extends JpaRepository<Cheer, Long> {

    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
            SELECT c.imageKey FROM Cheer c
                WHERE c.store = :store AND c.imageKey IS NOT NULL
                ORDER BY c.createdAt DESC
                LIMIT 1""")
    Optional<ImageKey> findRecentImageKey(Store store);

    @Query("""
            SELECT c.imageKey FROM Cheer c
                WHERE c.store = :store AND c.imageKey IS NOT NULL
                ORDER BY c.createdAt DESC""")
    List<ImageKey> findAllImageKey(Store store);

    int countByMember(Member member);

    boolean existsByMemberAndStoreKakaoId(Member member, String storeKakaoId);
}
