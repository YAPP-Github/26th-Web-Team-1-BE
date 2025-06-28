package timeeat.repository.member;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import timeeat.domain.member.Member;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findById(Long id);

    default Member getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_MEMBER_ID));
    }

    Optional<Member> findBySocialId(String socialId);
}
