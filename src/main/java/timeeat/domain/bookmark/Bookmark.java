package timeeat.domain.bookmark;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.domain.member.Member;
import timeeat.domain.store.Store;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Table(name = "bookmark")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    public Bookmark(Member member, Store store) {
        validateMember(member);
        validateStore(store);
        this.member = member;
        this.store = store;
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new BusinessException(BusinessErrorCode.BOOKMARK_MEMBER_REQUIRED);
        }
    }

    private void validateStore(Store store) {
        if (store == null) {
            throw new BusinessException(BusinessErrorCode.BOOKMARK_STORE_REQUIRED);
        }
    }
}

